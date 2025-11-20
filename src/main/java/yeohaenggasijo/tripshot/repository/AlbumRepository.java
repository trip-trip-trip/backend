package yeohaenggasijo.tripshot.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import yeohaenggasijo.tripshot.domain.album.Album;

import java.util.Optional;

public interface AlbumRepository extends JpaRepository<Album, Long> {

    // owner 기준으로 trip 에 연결된 앨범 찾기
    Optional<Album> findByTrip_IdAndOwner_Id(Long tripId, Long ownerId);
}
