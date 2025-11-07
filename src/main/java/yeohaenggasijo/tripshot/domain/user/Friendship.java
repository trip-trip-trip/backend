package yeohaenggasijo.tripshot.domain.user;

import yeohaenggasijo.tripshot.domain.base.BaseEntity;
import yeohaenggasijo.tripshot.domain.common.FriendshipStatus;
import jakarta.persistence.*;
import lombok.*;

@Getter @Setter
@NoArgsConstructor @AllArgsConstructor @Builder
@Entity
@Table(name = "friendships",
       uniqueConstraints = {
         @UniqueConstraint(name = "uk_friendships_pair", columnNames = { "requester_id", "addressee_id" })
       })
public class Friendship extends BaseEntity {
  @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  @ManyToOne(fetch = FetchType.LAZY) @JoinColumn(name = "requester_id", nullable = false)
  private User requester;

  @ManyToOne(fetch = FetchType.LAZY) @JoinColumn(name = "addressee_id", nullable = false)
  private User addressee;

  @Enumerated(EnumType.STRING)
  @Column(length = 12, nullable = false)
  private FriendshipStatus status;
}
