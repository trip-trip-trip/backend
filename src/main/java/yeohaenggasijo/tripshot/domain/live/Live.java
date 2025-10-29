package yeohaenggasijo.tripshot.domain.live;

import yeohaenggasijo.tripshot.domain.base.BaseEntity;
import yeohaenggasijo.tripshot.domain.common.LiveStatus;
import yeohaenggasijo.tripshot.domain.trip.Trip;
import yeohaenggasijo.tripshot.domain.user.User;
import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDateTime;

@Getter @Setter
@NoArgsConstructor @AllArgsConstructor @Builder
@Entity
@Table(name = "lives")
public class Live extends BaseEntity {
  @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  @ManyToOne(fetch = FetchType.LAZY) @JoinColumn(name = "trip_id", nullable = false)
  private Trip trip;

  @ManyToOne(fetch = FetchType.LAZY) @JoinColumn(name = "host_id", nullable = false)
  private User host;

  @Enumerated(EnumType.STRING)
  @Column(length = 10, nullable = false)
  private LiveStatus status;

  private LocalDateTime startedAt;
  private LocalDateTime endedAt;
}
