package yeohaenggasijo.tripshot.domain.user;

import jakarta.persistence.*;

@Entity
public class SocialAccount {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "user_id")
    private User user;

    private String provider;
    private String socialId;
    private String accessToken;


}
