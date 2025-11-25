package yeohaenggasijo.tripshot.service;

import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;
import yeohaenggasijo.tripshot.domain.user.User;
import yeohaenggasijo.tripshot.dto.trip.res.PostLocaListRes;
import yeohaenggasijo.tripshot.dto.trip.res.PostsLocaRes;
import yeohaenggasijo.tripshot.dto.user.req.UpdateMyProfileReq;
import yeohaenggasijo.tripshot.dto.user.res.MyProfileRes;
import yeohaenggasijo.tripshot.repository.FriendshipRepository;
import yeohaenggasijo.tripshot.repository.PostRepository;
import yeohaenggasijo.tripshot.repository.UserRepository;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import yeohaenggasijo.tripshot.service.storage.StorageUploader;
import yeohaenggasijo.tripshot.util.FileExtension;

@Service
@RequiredArgsConstructor
public class UserService {

    private final Logger logger = LoggerFactory.getLogger(UserService.class);
    private final UserRepository userRepository;
    private final FriendshipRepository friendshipRepository;
    private final PostRepository postRepository;
    private final StorageUploader storageUploader;
    private final FileExtension fileExtension;

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
    public MyProfileRes updateMyProfile(Long userId, MultipartFile file, UpdateMyProfileReq req) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("User not found: " + userId));

        if ((req.getUsername() != null)) {
            user.setUsername(req.getUsername());
        }
        if ((req.getBio() != null)) {
            user.setBio(req.getBio());
        }
        if (file != null) {
            // TODO: multipart 사진 스토리지에 업로드  / url 받아서 저장하고 반환
            String ctype = Optional.ofNullable(file.getContentType()).orElse("");
            String original = Optional.ofNullable(file.getOriginalFilename()).orElse("");
            String ext = fileExtension.guessExt(original, ctype, false);
            String key = "profiles/%d/%s.%s".formatted(userId, UUID.randomUUID(), ext);

            Path tmp = null;
            try {
                tmp = Files.createTempFile("upload-", "." + ext);
                file.transferTo(tmp.toFile());

                StorageUploader.UploadResult up = storageUploader.upload(tmp, ctype, key);
                String url = up.url();
                user.setAvatarUrl(url);
            } catch (IOException e) {
                logger.error("[ERROR] Error while uploading file: {}", e, e);
                throw new RuntimeException(e);
            }

        }
        if ((req.getTag() != null)) {
            String oldTag = user.getTag();
            if (!oldTag.isEmpty() && !oldTag.equals(req.getTag())) {
                Optional<User> check = userRepository.findByTag(req.getTag());

                if (check.isPresent()) {
                    throw new IllegalArgumentException("Tag already exists: " + req.getTag() + " -> user id: " + check.get().getId());
                }
            }
            user.setTag(req.getTag());
        }
        userRepository.save(user);

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
