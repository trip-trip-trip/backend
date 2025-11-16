package yeohaenggasijo.tripshot.domain.reel;

import yeohaenggasijo.tripshot.domain.base.BaseEntity;
import yeohaenggasijo.tripshot.domain.common.ReelRenderStatus;
import yeohaenggasijo.tripshot.domain.media.MediaAsset;
import yeohaenggasijo.tripshot.domain.trip.Trip;
import yeohaenggasijo.tripshot.domain.user.User;
import jakarta.persistence.*;
import lombok.*;

@Getter @Setter
@NoArgsConstructor @AllArgsConstructor @Builder
@Entity
@Table(name = "short_reels")
public class ShortReel extends BaseEntity {
  @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  @ManyToOne(fetch = FetchType.LAZY) @JoinColumn(name = "trip_id", nullable = false)
  private Trip trip;

  @ManyToOne(fetch = FetchType.LAZY) @JoinColumn(name = "creator_id", nullable = false)
  private User creator;

  @Column(length = 80, nullable = false)
  private String title;

  @ManyToOne(fetch = FetchType.LAZY) @JoinColumn(name = "output_media_id")
  private MediaAsset outputMedia;

  @Enumerated(EnumType.STRING)
  @Column(length = 12, nullable = false)
  private ReelRenderStatus renderStatus;
}
