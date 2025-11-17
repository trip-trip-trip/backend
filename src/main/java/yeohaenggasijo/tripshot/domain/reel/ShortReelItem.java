package yeohaenggasijo.tripshot.domain.reel;

import yeohaenggasijo.tripshot.domain.base.BaseEntity;
import yeohaenggasijo.tripshot.domain.common.ReelTransition;
import yeohaenggasijo.tripshot.domain.media.MediaAsset;
import jakarta.persistence.*;
import lombok.*;

@Getter @Setter
@NoArgsConstructor @AllArgsConstructor @Builder
@Entity
@Table(name = "short_reel_items",
       uniqueConstraints = {
         @UniqueConstraint(name = "uk_short_reel_items_reel_media", columnNames = { "reel_id", "media_id" })
       },
       indexes = {
         @Index(name = "idx_short_reel_items_reel_position", columnList = "reel_id, position")
       })
public class ShortReelItem extends BaseEntity {
  @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  @ManyToOne(fetch = FetchType.LAZY) @JoinColumn(name = "reel_id", nullable = false)
  private ShortReel reel;

  @ManyToOne(fetch = FetchType.LAZY) @JoinColumn(name = "media_id", nullable = false)
  private MediaAsset media;

  private Integer position;
  private Integer holdMs;

  @Enumerated(EnumType.STRING)
  @Column(length = 20, nullable = false)
  private ReelTransition transition;
}
