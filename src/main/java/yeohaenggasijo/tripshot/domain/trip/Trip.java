package yeohaenggasijo.tripshot.domain.trip;

import yeohaenggasijo.tripshot.domain.base.BaseEntity;
import yeohaenggasijo.tripshot.domain.common.TripStatus;
import yeohaenggasijo.tripshot.domain.common.TripVisibility;
import yeohaenggasijo.tripshot.domain.media.MediaAsset;
import yeohaenggasijo.tripshot.domain.place.Place;
import yeohaenggasijo.tripshot.domain.user.User;
import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDate;

@Getter @Setter
@NoArgsConstructor @AllArgsConstructor @Builder
@Entity
@Table(name = "trips")
public class Trip extends BaseEntity {
  @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  @ManyToOne(fetch = FetchType.LAZY) @JoinColumn(name = "owner_id", nullable = false)
  private User owner;

  @Column(length = 80, nullable = false)
  private String title;

  @Column(columnDefinition = "TEXT")
  private String description;

  @Enumerated(EnumType.STRING)
  @Column(length = 10, nullable = false)
  private TripVisibility visibility;

  @Enumerated(EnumType.STRING)
  @Column(length = 10, nullable = false)
  private TripStatus status;

  private LocalDate startDate;
  private LocalDate endDate;

  @ManyToOne(fetch = FetchType.LAZY) @JoinColumn(name = "place_id")
  private Place place;

  @ManyToOne(fetch = FetchType.LAZY) @JoinColumn(name = "cover_media_id")
  private MediaAsset coverMedia;

  @Column(length = 16)
  private String inviteCode;
}
