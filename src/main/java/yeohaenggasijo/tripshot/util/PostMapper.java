package yeohaenggasijo.tripshot.util;

import org.springframework.stereotype.Component;
import yeohaenggasijo.tripshot.domain.media.MediaAsset;
import yeohaenggasijo.tripshot.domain.post.Post;
import yeohaenggasijo.tripshot.domain.post.PostMedia;
import yeohaenggasijo.tripshot.domain.user.User;
import yeohaenggasijo.tripshot.dto.trip.req.MediaAttachmentReq;
import yeohaenggasijo.tripshot.dto.trip.res.AuthorRes;
import yeohaenggasijo.tripshot.dto.trip.res.MediaRes;
import yeohaenggasijo.tripshot.dto.trip.res.PostRes;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Component
public class PostMapper {
    // 정적 팩토리 메서드 toPostRes()구현
    public PostRes toPostRes(
            Post post,
            AuthorRes authorRes,
            List<MediaRes> mediaList,
            int likeCount,
            int commentCount,
            boolean isLiked,
            boolean isMe
    ){
      // 모든 인자를 받아서 PostRes DTO를 생성하는 로직
      return PostRes.builder()
              .id(post.getId())
              .author(authorRes)
              .caption(post.getCaption())
              // 엔티티에서 필드 추출/변환
              .tripId(post.getTrip().getId())
              .visibility(post.getVisibility().name()) // Enum -> String
              // 계산된 값 사용
              .likeCount(likeCount)
              .commentCount(commentCount)
              .isLiked(isLiked)
              .isMe(isMe)
              .media(mediaList)
              // BaseEntity에서 상속받은 필드
              .createdAt(post.getCreatedAt())
              .updatedAt(post.getUpdatedAt())
              .build();
    }

    // 1. User 엔티티를 AuthorRes DTO로 변환
    public AuthorRes toAuthorRes(User user) {
        return AuthorRes.builder()
                .id(user.getId())
                .username(user.getUsername())
                .avatarUrl(user.getAvatarUrl()) // User 엔티티에 avatarUrl 필드가 있다고 가정
                .build();
    }

    // MediaRes 단일 항목 변환 메서드 (DB Read용)
    // Service에서 가져온 PostMedia와 MediaAsset 엔티티를 받아서 DTO를 완성합니다.
    public MediaRes toMediaRes(PostMedia postMedia, MediaAsset mediaAsset) {
        return MediaRes.builder()
                // PostMedia에서 순서를 가져옴
                .position(postMedia.getPosition())
                // MediaAsset에서 상세 정보를 가져옴
                .id(mediaAsset.getId())
                .url(mediaAsset.getUrl())
                .thumbnailUrl(mediaAsset.getThumbnailUrl())
                .mediaKind(mediaAsset.getMediaKind().name()) // Enum -> String 변환 (MediaKind 엔티티에 name() 메서드 가정)
                .build();
    }

    // MediaRes 리스트 변환 메서드 (DB Read용)
    // Map 형태로 MediaAsset을 받아서 N+1 없이 빠르게 매핑합니다.
    public List<MediaRes> toMediaResList(List<PostMedia> postMediaList, Map<Long, MediaAsset> assetMap) {
        return postMediaList.stream()
                .map(pm -> {
                    MediaAsset asset = assetMap.get(pm.getObjectId());
                    // 미디어 에셋이 없는 경우 (데이터 오류)는 건너뜁니다.
                    if (asset == null) return null;
                    return toMediaRes(pm, asset);
                })
                .filter(java.util.Objects::nonNull)
                .collect(Collectors.toList());
    }

    // toMediaResList (게시글 작성 직후 응답용)
    // PostMedia 엔티티가 아닌 요청 DTO(MediaAttachmentReq)만 있을 때 사용합니다.
    public List<MediaRes> toMediaResListForCreation(List<MediaAttachmentReq> reqList) {
        // Service에서 MediaAssetRepository를 통해 URL 등을 조회한 후
        // 이 메서드를 호출해야 완벽하지만, 지금은 임시로 ID만 반환합니다.
        // 완벽한 구현을 위해서는 Service에서 MediaAsset 조회가 필수입니다.
        return reqList.stream()
                .map(req -> MediaRes.builder()
                        .id(req.getObjectId())
                        .build())
                .collect(Collectors.toList());
    }
}
