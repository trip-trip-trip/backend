package yeohaenggasijo.tripshot.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import yeohaenggasijo.tripshot.domain.common.FriendshipStatus;
import yeohaenggasijo.tripshot.domain.user.Friendship;
import yeohaenggasijo.tripshot.domain.user.User;
import yeohaenggasijo.tripshot.dto.user.req.*;
import yeohaenggasijo.tripshot.dto.user.res.*;
import yeohaenggasijo.tripshot.repository.FriendshipRepository;
import yeohaenggasijo.tripshot.repository.UserRepository;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class FriendshipService {

    private final UserRepository userRepository;
    private final FriendshipRepository friendshipRepository;

    // ================== 내부 유틸 ==================

    private User getUserOrThrow(Long id) {
        return userRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("User not found: " + id));
    }

    private FriendUserRes toUserRes(User user) {
        return new FriendUserRes(
                user.getId(),
                user.getUsername(),
                user.getTag(),
                user.getAvatarUrl(),
                user.getBio()
        );
    }

    // ================== 1. 친구 목록 조회 ==================

    @Transactional(readOnly = true)
    public List<FriendUserRes> getFriends(Long currentUserId) {
        List<Friendship> list = friendshipRepository
                .findByStatusAndRequester_IdOrStatusAndAddressee_Id(
                        FriendshipStatus.ACCEPTED, currentUserId,
                        FriendshipStatus.ACCEPTED, currentUserId
                );

        return list.stream()
                .map(f -> {
                    User friend = f.getRequester().getId().equals(currentUserId)
                            ? f.getAddressee()
                            : f.getRequester();
                    return toUserRes(friend);
                })
                .toList();
    }

    // ================== 2. 유저 검색 ==================

    @Transactional(readOnly = true)
    public List<UserSearchRes> searchUsers(Long currentUserId, String keyword) {
        List<User> users = userRepository
                .findByUsernameContainingIgnoreCaseOrTagContainingIgnoreCase(keyword, keyword);

        return users.stream()
                .filter(u -> !u.getId().equals(currentUserId)) // 자기 자신 제외
                .map(u -> {
                    boolean isFriend = friendshipRepository.isFriend(currentUserId, u.getId());

                    boolean pendingSent = friendshipRepository
                            .existsByRequester_IdAndAddressee_IdAndStatus(
                                    currentUserId, u.getId(), FriendshipStatus.PENDING);

                    boolean pendingReceived = friendshipRepository
                            .existsByRequester_IdAndAddressee_IdAndStatus(
                                    u.getId(), currentUserId, FriendshipStatus.PENDING);

                    return new UserSearchRes(
                            u.getId(),
                            u.getUsername(),
                            u.getTag(),
                            u.getAvatarUrl(),
                            u.getBio(),
                            isFriend,
                            pendingSent,
                            pendingReceived
                    );
                })
                .toList();
    }

    // ================== 3. 친구 요청 보내기 ==================

    @Transactional
    public FriendRequestStatusRes sendFriendRequest(Long currentUserId, FriendRequestCreateReq req) {

        Long targetUserId = req.targetUserId();

        if (currentUserId.equals(targetUserId)) {
            throw new IllegalArgumentException("Cannot send friend request to yourself.");
        }

        User requester = getUserOrThrow(currentUserId);
        User target = getUserOrThrow(targetUserId);

        // 이미 친구인지
        if (friendshipRepository.isFriend(currentUserId, targetUserId)) {
            throw new IllegalArgumentException("Already friends.");
        }

        // 내가 보낸 PENDING 요청이 이미 있는지
        if (friendshipRepository.existsByRequester_IdAndAddressee_IdAndStatus(
                currentUserId, targetUserId, FriendshipStatus.PENDING)) {
            throw new IllegalArgumentException("Friend request already pending.");
        }

        // 상대가 나에게 보낸 PENDING 요청이 있는지
        Optional<Friendship> reverse =
                friendshipRepository.findByRequester_IdAndAddressee_Id(targetUserId, currentUserId);

        if (reverse.isPresent() && reverse.get().getStatus() == FriendshipStatus.PENDING) {
            throw new IllegalArgumentException("This user already sent you a friend request.");
        }

        Friendship fr = Friendship.builder()
                .requester(requester)
                .addressee(target)
                .status(FriendshipStatus.PENDING)
                .build();

        friendshipRepository.save(fr);

        return new FriendRequestStatusRes(fr.getId(), fr.getStatus().name());
    }

    // ================== 4. 받은 친구 요청 목록 ==================

    @Transactional(readOnly = true)
    public List<PendingFriendRequestRes> getPendingRequests(Long currentUserId) {
        List<Friendship> list =
                friendshipRepository.findByAddressee_IdAndStatus(currentUserId, FriendshipStatus.PENDING);

        return list.stream()
                .map(f -> new PendingFriendRequestRes(
                        f.getId(),
                        f.getRequester().getId(),
                        f.getRequester().getUsername(),
                        f.getRequester().getTag(),
                        f.getRequester().getAvatarUrl()
                ))
                .toList();
    }

    // ================== 5. 친구 요청 수락/거절 ==================

    @Transactional
    public FriendRequestStatusRes handleFriendRequest(
            Long currentUserId,
            Long requestId,
            FriendRequestActionReq req
    ) {
        Friendship fr = friendshipRepository.findByIdAndAddressee_Id(requestId, currentUserId)
                .orElseThrow(() -> new IllegalArgumentException("Friend request not found."));

        if (fr.getStatus() != FriendshipStatus.PENDING) {
            throw new IllegalArgumentException("Friend request already processed.");
        }

        String action = req.action().toUpperCase();

        switch (action) {
            case "ACCEPT" -> fr.setStatus(FriendshipStatus.ACCEPTED);
            case "REJECT" -> fr.setStatus(FriendshipStatus.BLOCKED); // 필요하면 나중에 REJECTED enum 추가
            default -> throw new IllegalArgumentException("Invalid action: " + req.action());
        }

        return new FriendRequestStatusRes(fr.getId(), fr.getStatus().name());
    }
    // ================== 6. 특정 유저 프로필 조회 ==================

    @Transactional(readOnly = true)
    public UserProfileRes getUserProfile(Long currentUserId, Long profileUserId) {

        User target = getUserOrThrow(profileUserId);

        boolean isMe = currentUserId.equals(profileUserId);

        boolean isFriend = false;
        boolean pendingSent = false;
        boolean pendingReceived = false;

        if (isMe) {
            isFriend = true;
        } else {
            // 친구 여부
            isFriend = friendshipRepository.isFriend(currentUserId, profileUserId);

            if (!isFriend) {
                // 내가 보낸 PENDING 요청
                pendingSent = friendshipRepository
                        .existsByRequester_IdAndAddressee_IdAndStatus(
                                currentUserId, profileUserId, FriendshipStatus.PENDING);

                // 내가 받은 PENDING 요청
                pendingReceived = friendshipRepository
                        .existsByRequester_IdAndAddressee_IdAndStatus(
                                profileUserId, currentUserId, FriendshipStatus.PENDING);
            }
        }

        return new UserProfileRes(
                target.getId(),
                target.getUsername(),
                target.getTag(),
                target.getAvatarUrl(),
                target.getBio(),
                isMe,
                isFriend,
                pendingSent,
                pendingReceived
        );
    }
}
