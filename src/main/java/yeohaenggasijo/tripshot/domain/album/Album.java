package yeohaenggasijo.tripshot.domain.album;

import yeohaenggasijo.tripshot.domain.base.BaseEntity;
import yeohaenggasijo.tripshot.domain.media.MediaAsset;
import yeohaenggasijo.tripshot.domain.trip.Trip;
import yeohaenggasijo.tripshot.domain.user.User;
import jakarta.persistence.*;
import lombok.*;

@Getter @Setter
@NoArgsConstructor @AllArgsConstructor @Builder
@Entity
@Table(name = "albums")
public class Album extends BaseEntity {
  @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  @ManyToOne(fetch = FetchType.LAZY) @JoinColumn(name = "owner_id", nullable = false)
  private User owner;

  @ManyToOne(fetch = FetchType.LAZY) @JoinColumn(name = "trip_id")
  private Trip trip;

  @Column(length = 80, nullable = false)
  private String title;

  @ManyToOne(fetch = FetchType.LAZY) @JoinColumn(name = "cover_media_id")
  private MediaAsset coverMedia;

  private Boolean isShared;
}
