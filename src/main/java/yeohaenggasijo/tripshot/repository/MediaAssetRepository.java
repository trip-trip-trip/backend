package yeohaenggasijo.tripshot.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import yeohaenggasijo.tripshot.domain.common.ContentType;
import yeohaenggasijo.tripshot.domain.common.MediaKind;
import yeohaenggasijo.tripshot.domain.media.MediaAsset;

import java.util.List;
import java.util.Set;

public interface MediaAssetRepository extends JpaRepository<MediaAsset, Long> {
    // List<MediaAsset>을 반환하는 일반적인 메서드
    List<MediaAsset> findAllByIdIn(Set<Long> assetIds);

    // findMediaAssetMapByObjectIds를 List 반환으로 정의
    List<MediaAsset> findMediaAssetDetailByIdIn(Set<Long> assetIds);
    // 필드명이 objectId가 아닌 MediaAsset의 기본 키(Id)이므로 findMediaAssetDetailByIdIn이 적절

    // 전체 엔티티 조회
    @Query("SELECT ma FROM MediaAsset ma WHERE ma.id IN :assetIds")
    List<MediaAsset> findMediaAssetMapByObjectIdsQuery(@Param("assetIds") Set<Long> assetIds);


    List<MediaAsset> findByTrip_IdAndContentTypeOrderByTakenAt(Long tripId, ContentType contentType);

    List<MediaAsset> findByTrip_Id(Long tripId);
}
