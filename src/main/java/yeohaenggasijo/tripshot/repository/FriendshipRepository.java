package yeohaenggasijo.tripshot.repository;


import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import yeohaenggasijo.tripshot.domain.user.Friendship;
import yeohaenggasijo.tripshot.domain.common.FriendshipStatus;

import java.util.List;
import java.util.Optional;

public interface FriendshipRepository extends JpaRepository<Friendship, Long> {
    /*@Query("SELECT CASE WHEN COUNT(f) > 0 THEN TRUE ELSE FALSE END " +
            "FROM Friendship f " +
            "WHERE f.status = 'ACCEPTED' AND (" +            //'ACCEPTED' 로 수정
            // Case 1: user1이 요청자, user2가 수신자
            "    (f.requester.id = :userId1 AND f.addressee.id = :userId2) OR " +
            // Case 2: user2가 요청자, user1이 수신자
            "    (f.requester.id = :userId2 AND f.addressee.id = :userId1)" +
            ")")
    boolean isFriend(@Param("userId1") Long userId1, @Param("userId2") Long userId2);*/

    @Query("""
    SELECT CASE WHEN COUNT(f) > 0 THEN TRUE ELSE FALSE END
    FROM Friendship f
    WHERE f.status = yeohaenggasijo.tripshot.domain.common.FriendshipStatus.ACCEPTED
      AND (
           (f.requester.id = :userId1 AND f.addressee.id = :userId2)
        OR (f.requester.id = :userId2 AND f.addressee.id = :userId1)
      )
    """)
    boolean isFriend(@Param("userId1") Long userId1,
                     @Param("userId2") Long userId2);
    /**
     * 친구 목록 조회용
     * - 현재 유저가 requester 이거나 addressee 이면서
     *   status 가 ACCEPTED 인 친구 관계 목록
     */
    List<Friendship> findByStatusAndRequester_IdOrStatusAndAddressee_Id(
            FriendshipStatus statusForRequester, Long requesterId,
            FriendshipStatus statusForAddressee, Long addresseeId
    );

    /**
     * 내가 받은 요청 목록 (status = PENDING)
     */
    List<Friendship> findByAddressee_IdAndStatus(Long addresseeId, FriendshipStatus status);

    /**
     * 내가 보낸 요청 목록 (status = PENDING)
     */
    List<Friendship> findByRequester_IdAndStatus(Long requesterId, FriendshipStatus status);

    /**
     * 두 유저 사이에 특정 status 관계가 존재하는지 여부
     * - 예) PENDING 요청 중복 방지
     */
    boolean existsByRequester_IdAndAddressee_IdAndStatus(
            Long requesterId,
            Long addresseeId,
            FriendshipStatus status
    );

    /**
     * 요청 수락/거절 시, 나에게 온 요청인지 확인하기 위한 조회
     */
    Optional<Friendship> findByIdAndAddressee_Id(Long id, Long addresseeId);

    /**
     * 두 유저 사이의 관계 한 건 조회 (status 무관)
     */
    Optional<Friendship> findByRequester_IdAndAddressee_Id(Long requesterId, Long addresseeId);


}
