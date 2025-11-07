package yeohaenggasijo.tripshot.domain.post;

import yeohaenggasijo.tripshot.domain.base.BaseEntity;
import yeohaenggasijo.tripshot.domain.common.PostVisibility;
import yeohaenggasijo.tripshot.domain.trip.Trip;
import yeohaenggasijo.tripshot.domain.user.User;
import jakarta.persistence.*;
import lombok.*;
import java.math.BigDecimal;

@Getter @Setter
@NoArgsConstructor @AllArgsConstructor @Builder
@Entity
@Table(name = "posts")
public class Post extends BaseEntity {
  @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  @ManyToOne(fetch = FetchType.LAZY) @JoinColumn(name = "author_id", nullable = false)
  private User author;

  @ManyToOne(fetch = FetchType.LAZY) @JoinColumn(name = "trip_id", nullable = false)
  private Trip trip;

  @Column(columnDefinition = "TEXT")
  private String caption;

  @Enumerated(EnumType.STRING)
  @Column(length = 10, nullable = false)
  private PostVisibility visibility;

  @Column(precision = 10, scale = 7)
  private BigDecimal lat;

  @Column(precision = 10, scale = 7)
  private BigDecimal lng;
}
