package yeohaenggasijo.tripshot.domain.notify;

import yeohaenggasijo.tripshot.domain.base.BaseEntity;
import yeohaenggasijo.tripshot.domain.user.User;
import jakarta.persistence.*;
import lombok.*;

@Getter @Setter
@NoArgsConstructor @AllArgsConstructor @Builder
@Entity
@Table(name = "notification_settings")
public class NotificationSetting extends BaseEntity {
  @Id
  private Long userId;

  @OneToOne(fetch = FetchType.LAZY) @MapsId
  @JoinColumn(name = "user_id")
  private User user;

  private Boolean momentEnabled;
  private Integer timesPerDay;

  @Column(length = 64)
  private String timezone;
}
