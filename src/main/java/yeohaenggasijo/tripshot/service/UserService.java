package yeohaenggasijo.tripshot.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import yeohaenggasijo.tripshot.domain.user.User;
import yeohaenggasijo.tripshot.dto.trip.res.PostLocaListRes;
import yeohaenggasijo.tripshot.dto.trip.res.PostsLocaRes;
import yeohaenggasijo.tripshot.dto.user.req.UpdateMyProfileReq;
import yeohaenggasijo.tripshot.dto.user.res.MyProfileRes;
import yeohaenggasijo.tripshot.repository.FriendshipRepository;
import yeohaenggasijo.tripshot.repository.PostRepository;
import yeohaenggasijo.tripshot.repository.UserRepository;

import java.util.List;

@Service
@RequiredArgsConstructor
public class UserService {

    private final UserRepository userRepository;
    private final FriendshipRepository friendshipRepository;
    private final PostRepository postRepository;

    @Transactional
    public boolean isFriend(Long currentUserId, Long authorId) {
        if (currentUserId.equals(authorId)) {
            return true;
        }
        return friendshipRepository.isFriend(currentUserId, authorId);
    }

    @Transactional(readOnly = true)
    public User getUser(Long id) {
        return userRepository.findById(id).orElse(null);
    }

    // ================== 마이페이지: 내 프로필 조회 ==================

    @Transactional(readOnly = true)
    public MyProfileRes getMyProfile(Long userId) {
        User user = userRepository.findById(userId)
                // TODO: 프로젝트의 커스텀 예외 있으면 거기로 교체
                .orElseThrow(() -> new IllegalArgumentException("User not found: " + userId));

        return MyProfileRes.from(user);
    }

    // ================== 마이페이지: 내 프로필 수정 ==================

    @Transactional
    public MyProfileRes updateMyProfile(Long userId, UpdateMyProfileReq req) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("User not found: " + userId));

        if (req.getUsername() != null) {
            user.setUsername(req.getUsername());
        }
        if (req.getBio() != null) {
            user.setBio(req.getBio());
        }
        if (req.getAvatarUrl() != null) {
            user.setAvatarUrl(req.getAvatarUrl());
        }
        if (req.getMobile() != null) {
            user.setMobile(req.getMobile());
        }

        return MyProfileRes.from(user);
    }

    // ================== 마이페이지: 내 게시글 위치 조회 ==================

    @Transactional(readOnly = true)
    public PostLocaListRes getMyPosts(Long userId) {
        List<PostsLocaRes> posts = postRepository.findAllPostLocaByAuthorId(userId);

        return PostLocaListRes.builder()
                .posts(posts)
                .build();
    }
}
