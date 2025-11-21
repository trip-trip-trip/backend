package yeohaenggasijo.tripshot.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import yeohaenggasijo.tripshot.domain.user.User;

import java.util.List;
import java.util.Optional;

public interface UserRepository extends JpaRepository<User, Long> {
    Optional<User> findById(Long id);

    Optional<User> findByMobile(String mobile);

    /**
     * 친구 검색용:
     * - username 에 keyword 포함
     * - 또는 tag 에 keyword 포함
     */
    List<User> findByUsernameContainingIgnoreCaseOrTagContainingIgnoreCase(
            String usernameKeyword,
            String tagKeyword
    );

    Optional<User> findByTag(String tag);

    List<User> findByTagContainingIgnoreCase(String tag);


}
