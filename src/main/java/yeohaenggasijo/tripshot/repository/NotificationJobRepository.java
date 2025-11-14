package yeohaenggasijo.tripshot.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import yeohaenggasijo.tripshot.domain.common.NotificationStatus;
import yeohaenggasijo.tripshot.domain.notify.NotificationJob;
import yeohaenggasijo.tripshot.domain.user.User;

import java.time.LocalDateTime;
import java.util.List;

public interface NotificationJobRepository extends JpaRepository<NotificationJob, Long> {
    List<NotificationJob> findByStatusAndScheduledAtBefore(
            NotificationStatus status,
            LocalDateTime scheduledAt
    );List<NotificationJob> findByUser(User user);
}
