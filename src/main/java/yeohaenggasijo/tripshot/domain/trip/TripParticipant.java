package yeohaenggasijo.tripshot.domain.trip;

import yeohaenggasijo.tripshot.domain.base.BaseEntity;
import yeohaenggasijo.tripshot.domain.common.TripParticipantRole;
import yeohaenggasijo.tripshot.domain.user.User;
import jakarta.persistence.*;
import lombok.*;

@Getter @Setter
@NoArgsConstructor @AllArgsConstructor @Builder
@Entity
@Table(name = "trip_participants",
       uniqueConstraints = {
         @UniqueConstraint(name = "uk_trip_participants_trip_user", columnNames = { "trip_id", "user_id" })
       })
public class TripParticipant extends BaseEntity {
  @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  @ManyToOne(fetch = FetchType.LAZY) @JoinColumn(name = "trip_id", nullable = false)
  private Trip trip;

  @ManyToOne(fetch = FetchType.LAZY) @JoinColumn(name = "user_id", nullable = false)
  private User user;

  @Enumerated(EnumType.STRING)
  @Column(length = 12, nullable = false)
  private TripParticipantRole role;
}
