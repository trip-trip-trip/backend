package yeohaenggasijo.tripshot.domain.post;

import yeohaenggasijo.tripshot.domain.base.BaseEntity;
import yeohaenggasijo.tripshot.domain.common.PostVisibility;
import yeohaenggasijo.tripshot.domain.media.MediaAsset;
import yeohaenggasijo.tripshot.domain.place.Place;
import yeohaenggasijo.tripshot.domain.trip.Trip;
import yeohaenggasijo.tripshot.domain.user.User;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

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

  @ManyToOne(fetch = FetchType.LAZY) @JoinColumn(name = "place_id", nullable = false)
  private Place place;

  public void update(String caption, String visibility) {
    // 캡션 업데이트
    if (caption != null) {
      this.caption = caption;
    }

    // 공개 범위 업데이트 (String을 Enum으로 변환)
    if (visibility != null) {
      this.visibility = PostVisibility.valueOf(visibility.toUpperCase());
    }
    this.updatedAt = LocalDateTime.now();
  }

  @OneToMany(mappedBy = "post", cascade = CascadeType.ALL, orphanRemoval = true)
  private List<Comment> comments = new ArrayList<>();

  @OneToMany(mappedBy = "post", cascade = CascadeType.ALL, orphanRemoval = true)
  private List<Like> likes = new ArrayList<>();

  @OneToMany(mappedBy = "post", cascade = CascadeType.ALL, orphanRemoval = true)
  private List<PostMedia> postMedias = new ArrayList<>();
}
