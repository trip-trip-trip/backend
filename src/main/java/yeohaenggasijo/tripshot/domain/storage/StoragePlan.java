package yeohaenggasijo.tripshot.domain.storage;

import yeohaenggasijo.tripshot.domain.base.BaseEntity;
import jakarta.persistence.*;
import lombok.*;

@Getter @Setter
@NoArgsConstructor @AllArgsConstructor @Builder
@Entity
@Table(name = "storage_plans")
public class StoragePlan extends BaseEntity {
  @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  @Column(length = 40, nullable = false)
  private String name;

  private Integer quotaMb;
}
