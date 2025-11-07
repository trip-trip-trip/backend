package yeohaenggasijo.tripshot.domain.post;

import yeohaenggasijo.tripshot.domain.base.BaseEntity;
import yeohaenggasijo.tripshot.domain.common.AttachmentType;
import jakarta.persistence.*;
import lombok.*;

@Getter @Setter
@NoArgsConstructor @AllArgsConstructor @Builder
@Entity
@Table(name = "post_media",
       uniqueConstraints = {
         @UniqueConstraint(name = "uk_post_media_unique", columnNames = { "post_id", "object_type", "object_id" })
       },
       indexes = {
         @Index(name = "idx_post_media_post_position", columnList = "post_id, position")
       })
public class PostMedia extends BaseEntity {
  @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  @ManyToOne(fetch = FetchType.LAZY) @JoinColumn(name = "post_id", nullable = false)
  private Post post;

  @Enumerated(EnumType.STRING)
  @Column(name = "object_type", length = 12, nullable = false)
  private AttachmentType objectType;

  @Column(name = "object_id", nullable = false)
  private Long objectId;

  private Integer position;
}
