package yeohaenggasijo.tripshot.domain.media;

import yeohaenggasijo.tripshot.domain.base.BaseEntity;
import yeohaenggasijo.tripshot.domain.common.CaptureType;
import yeohaenggasijo.tripshot.domain.common.ContentType;
import yeohaenggasijo.tripshot.domain.common.MediaKind;
import yeohaenggasijo.tripshot.domain.trip.Trip;
import yeohaenggasijo.tripshot.domain.user.User;
import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDateTime;

@Getter @Setter
@NoArgsConstructor @AllArgsConstructor @Builder
@Entity
@Table(name = "media_assets",
       indexes = {
         @Index(name = "idx_media_assets_trip_created_at", columnList = "trip_id, createdAt")
       })
public class MediaAsset extends BaseEntity {
  @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  @ManyToOne(fetch = FetchType.LAZY) @JoinColumn(name = "uploader_id", nullable = false)
  private User uploader;

  @ManyToOne(fetch = FetchType.LAZY) @JoinColumn(name = "trip_id", nullable = false)
  private Trip trip;

  @Enumerated(EnumType.STRING)
  @Column(length = 10, nullable = false)
  private ContentType contentType;

  @Enumerated(EnumType.STRING)
  @Column(length = 10, nullable = false)
  private MediaKind mediaKind;

  @Enumerated(EnumType.STRING)
  @Column(length = 10, nullable = false)
  private CaptureType captureType;

  @Column(columnDefinition = "TEXT")
  private String comment;

  @Column(columnDefinition = "TEXT", nullable = false)
  private String url;

  @Column(columnDefinition = "TEXT")
  private String thumbnailUrl;

  private Integer width;
  private Integer height;
  private Integer durationSec;
  private LocalDateTime takenAt;
  private Boolean isSharedInAlbum;
  private LocalDateTime expiration;
}
