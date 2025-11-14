package yeohaenggasijo.tripshot.controller;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import nl.martijndwars.webpush.PushService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import yeohaenggasijo.tripshot.config.VapidKeys;
import yeohaenggasijo.tripshot.domain.common.NotificationStatus;
import yeohaenggasijo.tripshot.domain.common.NotificationType;
import yeohaenggasijo.tripshot.domain.common.SlotCode;
import yeohaenggasijo.tripshot.domain.notify.NotificationJob;
import yeohaenggasijo.tripshot.domain.notify.NotificationSetting;
import yeohaenggasijo.tripshot.domain.notify.Subscription;
import yeohaenggasijo.tripshot.domain.trip.Trip;
import yeohaenggasijo.tripshot.domain.user.User;
import yeohaenggasijo.tripshot.dto.push.NotificationSettingReq;
import yeohaenggasijo.tripshot.dto.push.SubscriptionReq;
import yeohaenggasijo.tripshot.repository.*;
import com.fasterxml.jackson.databind.ObjectMapper;
import yeohaenggasijo.tripshot.service.NotificationJobScheduler;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@RequiredArgsConstructor
@RestController
@RequestMapping("/push")
public class WebPushController {
    private final NotificationJobScheduler scheduler;
    private final VapidKeys vapidKeys;
    private final SubscriptionRepository subscriptionRepository;
    private final UserRepository userRepository;
    private final TripRepository tripRepository;
    private final NotificationSettingRepository notificationSettingRepository;
    private final NotificationJobRepository notificationJobRepository;
    private final ObjectMapper objectMapper;
    private final PushService pushService;

    // VAPID 공개 키를 프론트엔드에 전달하는 API

    @GetMapping("/vapid-key")
    public VapidPublicKeyResponse getVapidPublicKey() {
        // 프론트엔드가 키만 쉽게 가져가도록 JSON 형태로 반환
        return new VapidPublicKeyResponse(vapidKeys.getPublicKey());
    }

    // 응답 형태를 위한 Inner Class 또는 Record
    public record VapidPublicKeyResponse(String vapidPublicKey) {}

    @PostMapping("/subscribe")
    public ResponseEntity<?> subscribe(@RequestBody SubscriptionReq request) {
        try {
            Long userId = Long.valueOf(request.userId());

            User user = userRepository.findById(userId)
                    .orElseThrow(() -> new RuntimeException("User not found with ID: " + userId));

            // 기존 구독 찾기
            Optional<Subscription> existingSubscription =
                    subscriptionRepository.findByEndpoint(request.endpoint());

            Subscription subscription;
            if (existingSubscription.isPresent()) {
                // 기존 구독 업데이트
                subscription = existingSubscription.get();
                subscription.setUser(user);  // 사용자가 바뀔 수도 있으니
                subscription.setP256dh(request.p256dh());
                subscription.setAuth(request.auth());
                System.out.println("기존 구독 업데이트: " + request.endpoint());
            } else {
                // 새 구독 생성
                subscription = new Subscription();
                subscription.setUser(user);
                subscription.setEndpoint(request.endpoint());
                subscription.setP256dh(request.p256dh());
                subscription.setAuth(request.auth());
                System.out.println("새 구독 생성: " + request.endpoint());
            }

            subscriptionRepository.save(subscription);

            return ResponseEntity.ok(Map.of(
                    "success", true,
                    "message", "구독 완료"
            ));

        } catch (Exception e) {
            System.err.println("구독 실패: " + e.getMessage());
            e.printStackTrace();

            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of(
                            "success", false,
                            "message", e.getMessage()
                    ));
        }
    }

    @PostMapping("/settings")
    @Transactional
    public ResponseEntity<?> updateNotificationSettings(@RequestBody NotificationSettingReq request) {
        try {
            System.out.println("받은 요청: " + request);

            Long userId = request.userId();

            User user = userRepository.findById(userId)
                    .orElseThrow(() -> new RuntimeException("User not found: " + userId));

            NotificationSetting setting = notificationSettingRepository.findById(userId)
                    .orElseGet(() -> {
                        NotificationSetting newSetting = new NotificationSetting();
                        // @MapsId 사용 시 순서가 중요!
                        newSetting.setUser(user);  // 1. User 먼저 설정
                        // userId는 자동으로 설정됨
                        newSetting.setMomentEnabled(true);
                        return newSetting;
                    });

            setting.setTimesPerDay(request.timesPerDay());

            String timezoneJson = objectMapper.writeValueAsString(request.timezone());
            setting.setTimezone(timezoneJson);

            System.out.println("저장: " + request.timezone().get(0) + "시~" +
                    request.timezone().get(1) + "시 사이 " +
                    request.timesPerDay() + "회");

            notificationSettingRepository.save(setting);
            System.out.println("설정 저장 완료");

            scheduler.scheduleJobsForToday(user, setting);

            // 여행 중인지 확인해서 메시지 변경
            LocalDate today = LocalDate.now();
            List<Trip> activeTrips = tripRepository.findActiveTrips(userId, today);

            String message = activeTrips.isEmpty()
                    ? "설정 저장 완료! 여행 시작 시 알림이 전송됩니다."
                    : "설정 저장 완료! 오늘부터 알림이 시작됩니다.";

            return ResponseEntity.ok(Map.of(
                    "isSuccess", true,
                    "message", message
            ));

        } catch (Exception e) {
            System.err.println("설정 저장 중 에러: " + e.getMessage());
            e.printStackTrace();

            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of(
                            "isSuccess", false,
                            "code", 500,
                            "message", e.getMessage(),
                            "result", null
                    ));
        }
    }

    @PostMapping("/test/{userId}")
    public void sendTestPush(@PathVariable Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found"));

        List<Subscription> subscriptions = subscriptionRepository.findByUser(user);

        if (subscriptions.isEmpty()) {
            throw new RuntimeException("No subscriptions found for user");
        }

        String payload = "{\"title\":\"테스트 알림\", \"body\":\"푸시 알림이 정상 작동합니다!\", \"icon\":\"/icon.png\"}";

        for (Subscription sub : subscriptions) {
            try {
                nl.martijndwars.webpush.Subscription pushSub =
                        new nl.martijndwars.webpush.Subscription(
                                sub.getEndpoint(),
                                new nl.martijndwars.webpush.Subscription.Keys(
                                        sub.getP256dh(),
                                        sub.getAuth()
                                )
                        );

                nl.martijndwars.webpush.Notification notification =
                        new nl.martijndwars.webpush.Notification(pushSub, payload);

                pushService.send(notification);
            } catch (Exception e) {
                throw new RuntimeException("Push send failed", e);
            }
        }
    }

    @PostMapping("/test/schedule-immediate/{userId}")
    public ResponseEntity<?> scheduleImmediate(@PathVariable Long userId) {
        try {
            User user = userRepository.findById(userId)
                    .orElseThrow(() -> new RuntimeException("User not found"));

            // 1분, 2분, 3분 뒤에 발송될 Job 생성
            for (int i = 1; i <= 3; i++) {
                NotificationJob job = new NotificationJob();
                job.setUser(user);
                job.setType(NotificationType.MOMENT_REMINDER);
                job.setStatus(NotificationStatus.QUEUED);
                job.setScheduledAt(LocalDateTime.now().plusMinutes(i));
                job.setSlotCode(SlotCode.AFTERNOON);

                notificationJobRepository.save(job);
                System.out.println("Job 생성: " + job.getScheduledAt());
            }

            return ResponseEntity.ok(Map.of(
                    "success", true,
                    "message", "1분, 2분, 3분 뒤 알림 예약 완료! 기다려보세요."
            ));
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(500)
                    .body(Map.of("success", false, "message", e.getMessage()));
        }
    }

    /**
     * 테스트용: 생성된 Job 목록 조회
     */
    @GetMapping("/test/jobs/{userId}")
    public ResponseEntity<?> getJobs(@PathVariable Long userId) {
        try {
            User user = userRepository.findById(userId)
                    .orElseThrow(() -> new RuntimeException("User not found"));

            List<NotificationJob> jobs = notificationJobRepository.findByUser(user);

            return ResponseEntity.ok(Map.of(
                    "success", true,
                    "count", jobs.size(),
                    "jobs", jobs.stream().map(job -> Map.of(
                            "id", job.getId(),
                            "scheduledAt", job.getScheduledAt().toString(),
                            "status", job.getStatus().toString(),
                            "type", job.getType().toString()
                    )).toList()
            ));
        } catch (Exception e) {
            return ResponseEntity.status(500)
                    .body(Map.of("success", false, "message", e.getMessage()));
        }
    }
}
