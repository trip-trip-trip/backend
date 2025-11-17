package yeohaenggasijo.tripshot.domain.storage;

import yeohaenggasijo.tripshot.domain.base.BaseEntity;
import yeohaenggasijo.tripshot.domain.user.User;
import jakarta.persistence.*;
import lombok.*;

@Getter @Setter
@NoArgsConstructor @AllArgsConstructor @Builder
@Entity
@Table(name = "user_storage")
public class UserStorage extends BaseEntity {
  @Id
  private Long userId;

  @OneToOne(fetch = FetchType.LAZY) @MapsId
  @JoinColumn(name = "user_id")
  private User user;

  @ManyToOne(fetch = FetchType.LAZY) @JoinColumn(name = "plan_id", nullable = false)
  private StoragePlan plan;

  private Integer usage;
}
