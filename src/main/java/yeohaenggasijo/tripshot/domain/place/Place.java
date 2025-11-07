package yeohaenggasijo.tripshot.domain.place;

import yeohaenggasijo.tripshot.domain.base.BaseEntity;
import yeohaenggasijo.tripshot.domain.common.PlaceType;
import jakarta.persistence.*;
import lombok.*;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

@Getter @Setter
@NoArgsConstructor @AllArgsConstructor @Builder
@Entity
@Table(name = "places")
public class Place extends BaseEntity {
  @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  @Column(length = 80, nullable = false)
  private String name;

  @Enumerated(EnumType.STRING)
  @Column(length = 16, nullable = false)
  private PlaceType type;

  @ManyToOne(fetch = FetchType.LAZY) @JoinColumn(name = "parent_id")
  private Place parent;

  @OneToMany(mappedBy = "parent")
  @Builder.Default
  private List<Place> children = new ArrayList<>();

  @Column(precision = 10, scale = 7)
  private BigDecimal lat;

  @Column(precision = 10, scale = 7)
  private BigDecimal lng;
}
