package yeohaenggasijo.tripshot.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import yeohaenggasijo.tripshot.domain.user.User;
import yeohaenggasijo.tripshot.repository.FriendshipRepository;
import yeohaenggasijo.tripshot.repository.UserRepository;

@Service
@RequiredArgsConstructor
public class UserService {
    private final UserRepository userRepository;
    private final FriendshipRepository friendshipRepository;

    @Transactional
    public boolean isFriend(Long currentUserId, Long authorId) {
        // 1. 자기 자신의 포스트인 경우 (FriendshipService에서 처리할 일은 아니지만 안전 코드)
        if (currentUserId.equals(authorId)) {
            return true;
        }

        // 2. Repository의 쿼리 메서드를 사용해 친구 관계를 확인
        return friendshipRepository.isFriend(currentUserId, authorId);
    }

    @Transactional(readOnly = true)
    public User getUser(Long id) {
        return userRepository.findById(id).orElse(null);
    }
}
