package yeohaenggasijo.tripshot.domain.user;

import yeohaenggasijo.tripshot.domain.base.BaseEntity;
import jakarta.persistence.*;
import lombok.*;

@Getter @Setter
@NoArgsConstructor @AllArgsConstructor @Builder
@Entity
@Table(name = "users",
       uniqueConstraints = {
         @UniqueConstraint(name = "uk_users_username", columnNames = "username"),
         @UniqueConstraint(name = "uk_users_email", columnNames = "email")
       })
public class User extends BaseEntity {
  @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  @Column(length = 30, nullable = false)
  private String username;

  @Column(length = 120)
  private String email;

  @Column(length = 255)
  private String passwordHash;

  @Column(length = 160)
  private String bio;

  @Column(columnDefinition = "TEXT")
  private String avatarUrl;

  @Column
  private String mobile;

}
