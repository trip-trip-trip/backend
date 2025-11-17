package yeohaenggasijo.tripshot.domain.notify;

import yeohaenggasijo.tripshot.domain.base.BaseEntity;
import yeohaenggasijo.tripshot.domain.common.NotificationStatus;
import yeohaenggasijo.tripshot.domain.common.NotificationType;
import yeohaenggasijo.tripshot.domain.common.SlotCode;
import yeohaenggasijo.tripshot.domain.trip.Trip;
import yeohaenggasijo.tripshot.domain.user.User;
import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDateTime;

@Getter @Setter
@NoArgsConstructor @AllArgsConstructor @Builder
@Entity
@Table(name = "notification_jobs",
       indexes = {
         @Index(name = "idx_notification_jobs_scheduled_at", columnList = "scheduledAt")
       })
public class NotificationJob extends BaseEntity {
  @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  @ManyToOne(fetch = FetchType.LAZY) @JoinColumn(name = "user_id", nullable = false)
  private User user;

  @ManyToOne(fetch = FetchType.LAZY) @JoinColumn(name = "trip_id")
  private Trip trip;

  @Enumerated(EnumType.STRING) @Column(length = 24, nullable = false)
  private NotificationType type;

  @Enumerated(EnumType.STRING) @Column(length = 16, nullable = false)
  private SlotCode slotCode;

  private Integer weight;
  private LocalDateTime scheduledAt;
  private LocalDateTime sentAt;

  @Enumerated(EnumType.STRING) @Column(length = 16, nullable = false)
  private NotificationStatus status;

  @Column(columnDefinition = "TEXT")
  private String errorMessage;
}
