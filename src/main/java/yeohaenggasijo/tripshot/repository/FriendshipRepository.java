package yeohaenggasijo.tripshot.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import yeohaenggasijo.tripshot.domain.user.Friendship;

public interface FriendshipRepository extends JpaRepository<Friendship, Long> {
    @Query("SELECT CASE WHEN COUNT(f) > 0 THEN TRUE ELSE FALSE END " +
            "FROM Friendship f " +
            "WHERE f.status = 'accepted' AND (" +
            // Case 1: user1이 요청자, user2가 수신자
            "    (f.requester.id = :userId1 AND f.addressee.id = :userId2) OR " +
            // Case 2: user2가 요청자, user1이 수신자
            "    (f.requester.id = :userId2 AND f.addressee.id = :userId1)" +
            ")")
    boolean isFriend(@Param("userId1") Long userId1, @Param("userId2") Long userId2);
}
