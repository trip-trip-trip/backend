package yeohaenggasijo.tripshot.domain.notify;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import yeohaenggasijo.tripshot.domain.user.User;

@Getter
@Setter
@Entity
@NoArgsConstructor
@Table(name = "push_subscription")
public class Subscription {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // 누가 구독했는지 알기 위한 사용자 ID. 실제 앱에서는 User 엔티티와 관계를 맺어야 함.
    @ManyToOne(fetch = FetchType.LAZY) @JoinColumn(name = "user_id", nullable = false)
    private User user;

    // 알림을 보낼 고유 주소 (URL 형태)
    @Column(length = 512, nullable = false, unique = true) // 길고 유니크해야 함
    private String endpoint;

    // 암호화 키
    @Column(length = 255, nullable = false)
    private String p256dh;

    // 인증 키
    @Column(length = 255, nullable = false)
    private String auth;
}
