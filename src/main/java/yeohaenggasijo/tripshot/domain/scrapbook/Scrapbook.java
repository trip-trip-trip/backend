package yeohaenggasijo.tripshot.domain.scrapbook;

import yeohaenggasijo.tripshot.domain.base.BaseEntity;
import yeohaenggasijo.tripshot.domain.common.ScrapbookRenderStatus;
import yeohaenggasijo.tripshot.domain.common.ScrapbookVisibility;
import yeohaenggasijo.tripshot.domain.media.MediaAsset;
import yeohaenggasijo.tripshot.domain.scrapbook.ScrapbookTemplate;
import yeohaenggasijo.tripshot.domain.trip.Trip;
import yeohaenggasijo.tripshot.domain.user.User;
import jakarta.persistence.*;
import lombok.*;

@Getter @Setter
@NoArgsConstructor @AllArgsConstructor @Builder
@Entity
@Table(name = "scrapbooks")
public class Scrapbook extends BaseEntity {
  @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  @ManyToOne(fetch = FetchType.LAZY) @JoinColumn(name = "trip_id", nullable = false)
  private Trip trip;

  @ManyToOne(fetch = FetchType.LAZY) @JoinColumn(name = "creator_id", nullable = false)
  private User creator;

  @ManyToOne(fetch = FetchType.LAZY) @JoinColumn(name = "template_id")
  private ScrapbookTemplate template;

  @Column(length = 80, nullable = false)
  private String title;

  @ManyToOne(fetch = FetchType.LAZY) @JoinColumn(name = "cover_media_id")
  private MediaAsset coverMedia;

  @Enumerated(EnumType.STRING)
  @Column(length = 10, nullable = false)
  private ScrapbookVisibility visibility;

  @Enumerated(EnumType.STRING)
  @Column(length = 12, nullable = false)
  private ScrapbookRenderStatus renderStatus;
}
