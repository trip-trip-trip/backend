package yeohaenggasijo.tripshot.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import yeohaenggasijo.tripshot.domain.common.NotificationStatus;
import yeohaenggasijo.tripshot.domain.common.NotificationType;
import yeohaenggasijo.tripshot.domain.common.SlotCode;
import yeohaenggasijo.tripshot.domain.notify.NotificationJob;
import yeohaenggasijo.tripshot.domain.notify.NotificationSetting;
import yeohaenggasijo.tripshot.domain.trip.Trip;
import yeohaenggasijo.tripshot.domain.user.User;
import yeohaenggasijo.tripshot.repository.NotificationJobRepository;
import yeohaenggasijo.tripshot.repository.NotificationSettingRepository;
import yeohaenggasijo.tripshot.repository.TripRepository;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class NotificationJobScheduler {
    private final NotificationJobRepository jobRepository;
    private final NotificationSettingRepository settingRepository;
    private final ObjectMapper objectMapper;
    private final TripRepository tripRepository;

    // 활성화된 여행이 있는 경우만 Job 생성
    @Scheduled(cron = "0 0 0 * * *")
    @Transactional
    public void scheduleDailyJobs() {
        List<NotificationSetting> allSettings = settingRepository.findAll();
        LocalDate today = LocalDate.now();

        for (NotificationSetting setting : allSettings) {
            if (!setting.isMomentEnabled()) continue;

            List<Trip> activeTrips = tripRepository.findActiveTrips(
                    setting.getUserId(),
                    today
            );

            // 현재 진행 중인 여행이 있는지 확인
            if (activeTrips.isEmpty()) {
                System.out.println("❌ " + setting.getUser().getUsername() + " - 진행 중인 여행 없음");
                continue;
            }
            // 여행 중이면 Job 생성!
            Trip activeTrip = activeTrips.get(0);

            // timezone에서 시작/종료 시간 파싱
            int startHour = setting.getStartHour();
            int endHour = setting.getEndHour();

            System.out.println(setting.getUser().getUsername() + " - " + startHour + "시~" + endHour + "시 사이 " + setting.getTimesPerDay() + "회 스케줄링");

            List<LocalDateTime> randomTimes = generateRandomTimes(
                    today,
                    setting.getTimesPerDay(),
                    startHour,
                    endHour
            );

            for (LocalDateTime time : randomTimes) {
                NotificationJob job = new NotificationJob();
                job.setUser(setting.getUser());
                job.setTrip(activeTrip);
                job.setType(NotificationType.MOMENT_REMINDER);
                job.setStatus(NotificationStatus.QUEUED);
                job.setScheduledAt(time);
                job.setSlotCode(determineSlotCode(time));

                jobRepository.save(job);
                System.out.println("Job 생성: " + time);
            }
        }
    }

    /**
     * 특정 사용자의 오늘 남은 시간 Job 생성
     * 설정 저장 직후 호출됨
     */
    @Transactional
    public void scheduleJobsForToday(User user, NotificationSetting setting) {
        if (!setting.isMomentEnabled()) {
            System.out.println("❌ " + user.getUsername() + " - 알림 비활성화");
            return;
        }

        LocalDate today = LocalDate.now();
        LocalDateTime now = LocalDateTime.now();

        // 여행 중인지 확인
        List<Trip> activeTrips = tripRepository.findActiveTrips(user.getId(), today);

        if (activeTrips.isEmpty()) {
            System.out.println("❌ " + user.getUsername() + " - 진행 중인 여행 없음. Job 생성 안 함.");
            return;
        }
        Trip activeTrip = activeTrips.get(0);

        int startHour = setting.getStartHour();
        int endHour = setting.getEndHour();

        System.out.println("✅ " + user.getUsername() +
                " - " + startHour + "시~" + endHour + "시 사이 " +
                setting.getTimesPerDay() + "회 스케줄링");

        // 오늘 남은 시간에 생성
        List<LocalDateTime> randomTimes = generateRandomTimes(
                today,
                setting.getTimesPerDay(),
                startHour,
                endHour
        );

        // 이미 지난 시간은 제외
        List<LocalDateTime> futureTimes = randomTimes.stream()
                .filter(time -> time.isAfter(now))
                .toList();

        System.out.println("  📅 오늘 남은 알림: " + futureTimes.size() + "개");

        for (LocalDateTime time : futureTimes) {
            NotificationJob job = new NotificationJob();
            job.setUser(user);
            job.setTrip(activeTrip);
            job.setType(NotificationType.MOMENT_REMINDER);
            job.setStatus(NotificationStatus.QUEUED);
            job.setScheduledAt(time);
            job.setSlotCode(determineSlotCode(time));

            jobRepository.save(job);
            System.out.println("  📍 Job 생성: " + time);
        }
    }

    private List<LocalDateTime> generateRandomTimes(LocalDate date, int count, int startHour, int endHour) {
        List<LocalDateTime> times = new ArrayList<>();
        Random random = new Random();

        int hourRange = endHour - startHour;

        if (hourRange < 0) {
            throw new IllegalArgumentException("종료 시간이 시작 시간보다 늦어야 합니다");
        }

        for (int i = 0; i < count; i++) {
            int hour = startHour + random.nextInt(hourRange + 1);
            int minute = random.nextInt(60);
            times.add(LocalDateTime.of(date, LocalTime.of(hour, minute)));
        }

        return times.stream().sorted().collect(Collectors.toList());
    }

    private SlotCode determineSlotCode(LocalDateTime time) {
        int hour = time.getHour();
        if (hour < 6) return SlotCode.DAWN;
        if (hour < 9) return SlotCode.MORNING;
        if (hour < 12) return SlotCode.LUNCH;
        if (hour < 18) return SlotCode.AFTERNOON;
        if (hour < 21) return SlotCode.EVENING;
        return SlotCode.NIGHT;
    }
}