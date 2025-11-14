package yeohaenggasijo.tripshot.service;
import lombok.RequiredArgsConstructor;
import nl.martijndwars.webpush.Notification;
import nl.martijndwars.webpush.PushService;
import nl.martijndwars.webpush.Subscription; // web-push 라이브러리의 Subscription 객체
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import yeohaenggasijo.tripshot.config.VapidKeys;
import yeohaenggasijo.tripshot.domain.notify.NotificationJob;
import yeohaenggasijo.tripshot.domain.user.User;
import yeohaenggasijo.tripshot.repository.NotificationJobRepository;
import yeohaenggasijo.tripshot.repository.SubscriptionRepository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.concurrent.ExecutionException;
import yeohaenggasijo.tripshot.domain.common.NotificationStatus;
import java.util.stream.Collectors;


@Service
@RequiredArgsConstructor
public class NotificationJobSender {
    private final VapidKeys vapidKeys;
    private final NotificationJobRepository jobRepository;
    private final SubscriptionRepository subscriptionRepository;
    private final PushService pushService; // WebPushConfig에서 Bean으로 등록된 객체가 주입됨

    /**
     * 1분마다 실행되어 발송할 시간이 된 알림 Job을 처리
     */
    @Scheduled(cron = "0 * * * * *") //
    @Transactional
    public void sendScheduledJobs() {
        LocalDateTime now = LocalDateTime.now();

        // 1. 발송 대상 Job 조회: 예정 시각이 지났고 상태가 'queued'인 Job
        List<NotificationJob> jobsToSend =
                jobRepository.findByStatusAndScheduledAtBefore(NotificationStatus.QUEUED, now);

        for (NotificationJob job : jobsToSend) {
            User user = job.getUser(); // User 엔티티는 Job 엔티티에 이미 연결되어 있어야 함

            // 2. 해당 유저의 모든 활성 Subscription 조회
            // 이 findByUser는 SubscriptionRepository에 추가해야 합니다. (아래 3번 참고)
            List<yeohaenggasijo.tripshot.domain.notify.Subscription> userSubscriptions =
                    subscriptionRepository.findByUser(user);

            if (userSubscriptions.isEmpty()) {
                job.setStatus(NotificationStatus.CANCELED); // 구독 정보가 없으면 취소 처리
                jobRepository.save(job);
                continue;
            }

            try {
                System.out.println("푸시 발송 시도...");
                // 3. 실제 푸시 알림 발송
                sendPushNotification(job, userSubscriptions);

                // 4. 발송 성공 시 상태 업데이트
                job.setStatus(NotificationStatus.SENT);
                job.setSentAt(now);
                System.out.println("발송 성공!");

            } catch (Exception e) {
                // 5. 발송 실패 시 상태 업데이트
                job.setStatus(NotificationStatus.FAILED);
                job.setErrorMessage(e.getMessage());
                System.err.println("Failed to send push notification for job " + job.getId() + ": " + e.getMessage());
                e.printStackTrace();
            } finally {
                jobRepository.save(job);
            }
        }
    }

    private void sendPushNotification(NotificationJob job,
                                      List<yeohaenggasijo.tripshot.domain.notify.Subscription> userSubscriptions)
            throws ExecutionException,
            InterruptedException,
            java.security.GeneralSecurityException,
            org.jose4j.lang.JoseException{

        String payload = createPayload(job); // 알림 내용 (JSON 형태) 생성

        for (yeohaenggasijo.tripshot.domain.notify.Subscription sub : userSubscriptions) {

            // web-push 라이브러리가 요구하는 Subscription 객체로 변환
            Subscription pushSubscription = new Subscription(
                    sub.getEndpoint(),
                    new Subscription.Keys(sub.getP256dh(), sub.getAuth())
            );

            // 알림 객체 생성
            Notification notification = new Notification(pushSubscription, payload);

            try {
                // PushService.send() 호출을 try-catch로 감싸서 IOException을 처리
                pushService.send(notification);
                System.out.println("    → 푸시 전송 완료: " + sub.getEndpoint().substring(0, 50) + "...");
            } catch (java.io.IOException e) {
                // IOException 발생 시, 런타임 예외로 감싸서 상위 catch 블록으로 던짐
                throw new RuntimeException("PushService network error: " + e.getMessage(), e);
            }
        }
    }

    /**
     * 알림 메시지 페이로드 (실제 사용자에게 보여질 내용) 생성
     */
    private String createPayload(NotificationJob job) {
        // 알림 페이로드는 프론트엔드와 협의한 JSON 구조로 만들어야 합니다.

        String title = "여행 기록 알림";
        String body = "오늘의 순간을 기록할 시간이에요! 앱을 열어보세요.";

        // NotificationJob의 type을 태그로 사용
        return String.format(
                "{\"title\":\"%s\", \"body\":\"%s\", \"icon\":\"/path/to/icon.png\", \"tag\":\"%s\"}",
                title, body, job.getType()
        );
    }
}
