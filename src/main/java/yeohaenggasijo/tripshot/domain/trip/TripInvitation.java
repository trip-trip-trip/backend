package yeohaenggasijo.tripshot.domain.trip;

import yeohaenggasijo.tripshot.domain.base.BaseEntity;
import yeohaenggasijo.tripshot.domain.common.InvitationStatus;
import yeohaenggasijo.tripshot.domain.user.User;
import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDateTime;

@Getter @Setter
@NoArgsConstructor @AllArgsConstructor @Builder
@Entity
@Table(name = "trip_invitations",
       uniqueConstraints = {
         @UniqueConstraint(name = "uk_trip_invitations_trip_invitee", columnNames = { "trip_id", "invitee_id" })
       })
public class TripInvitation extends BaseEntity {
  @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  @ManyToOne(fetch = FetchType.LAZY) @JoinColumn(name = "trip_id", nullable = false)
  private Trip trip;

  @ManyToOne(fetch = FetchType.LAZY) @JoinColumn(name = "inviter_id", nullable = false)
  private User inviter;

  @ManyToOne(fetch = FetchType.LAZY) @JoinColumn(name = "invitee_id", nullable = false)
  private User invitee;

  @Enumerated(EnumType.STRING)
  @Column(length = 10, nullable = false)
  private InvitationStatus status;

  private LocalDateTime respondedAt;
}
