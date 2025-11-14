package yeohaenggasijo.tripshot.domain.notify;

import yeohaenggasijo.tripshot.domain.base.BaseEntity;
import yeohaenggasijo.tripshot.domain.common.DevicePlatform;
import yeohaenggasijo.tripshot.domain.user.User;
import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDateTime;

@Getter @Setter
@NoArgsConstructor @AllArgsConstructor @Builder
@Entity
@Table(name = "push_devices",
       uniqueConstraints = {
         @UniqueConstraint(name = "uk_push_devices_platform_token", columnNames = { "platform", "deviceToken" })
       })
public class PushDevice extends BaseEntity {
  @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  @ManyToOne(fetch = FetchType.LAZY) @JoinColumn(name = "user_id", nullable = false)
  private User user;

  @Enumerated(EnumType.STRING)
  @Column(length = 12, nullable = false)
  private DevicePlatform platform;

  @Column(length = 256, nullable = false)
  private String deviceToken;

  private Boolean isActive;
  private LocalDateTime lastSeenAt;


}
