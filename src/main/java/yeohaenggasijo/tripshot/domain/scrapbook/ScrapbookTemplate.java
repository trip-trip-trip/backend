package yeohaenggasijo.tripshot.domain.scrapbook;

import yeohaenggasijo.tripshot.domain.base.BaseEntity;
import jakarta.persistence.*;
import lombok.*;

@Getter @Setter
@NoArgsConstructor @AllArgsConstructor @Builder
@Entity
@Table(name = "scrapbook_templates")
public class ScrapbookTemplate extends BaseEntity {
  @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  @Column(length = 50, nullable = false)
  private String name;

  @Column(columnDefinition = "TEXT")
  private String previewUrl;

  @Column(columnDefinition = "TEXT")
  private String layoutJson;

  private Integer maxMediaCount;
}
