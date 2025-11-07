package yeohaenggasijo.tripshot.domain.post;

import yeohaenggasijo.tripshot.domain.base.BaseEntity;
import yeohaenggasijo.tripshot.domain.user.User;
import jakarta.persistence.*;
import lombok.*;

@Getter @Setter
@NoArgsConstructor @AllArgsConstructor @Builder
@Entity
@Table(name = "likes",
       uniqueConstraints = {
         @UniqueConstraint(name = "uk_likes_post_user_type", columnNames = { "post_id", "user_id", "type" })
       })
public class Like extends BaseEntity {
  @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  @ManyToOne(fetch = FetchType.LAZY) @JoinColumn(name = "post_id", nullable = false)
  private Post post;

  @ManyToOne(fetch = FetchType.LAZY) @JoinColumn(name = "user_id", nullable = false)
  private User user;

  @Column(length = 32, nullable = false)
  private String type;
}
