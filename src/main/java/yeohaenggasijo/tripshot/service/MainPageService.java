package yeohaenggasijo.tripshot.service;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import yeohaenggasijo.tripshot.domain.media.MediaAsset;
import yeohaenggasijo.tripshot.domain.post.Comment;
import yeohaenggasijo.tripshot.domain.post.Like;
import yeohaenggasijo.tripshot.domain.post.Post;
import yeohaenggasijo.tripshot.domain.post.PostMedia;
import yeohaenggasijo.tripshot.domain.trip.Trip;
import yeohaenggasijo.tripshot.domain.user.User;
import yeohaenggasijo.tripshot.dto.trip.req.CreateCommentReq;
import yeohaenggasijo.tripshot.dto.trip.req.CreatePostReq;
import yeohaenggasijo.tripshot.dto.trip.req.MediaAttachmentReq;
import yeohaenggasijo.tripshot.dto.trip.req.UpdatePostReq;
import yeohaenggasijo.tripshot.dto.trip.res.*;
import yeohaenggasijo.tripshot.repository.*;
import yeohaenggasijo.tripshot.util.PostMapper;

import java.time.LocalDateTime;
import java.time.format.DateTimeParseException;
import java.util.*;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class MainPageService {
    private final PostRepository postRepository;
    private final PostMapper postMapper;
    private final CommentRepository commentRepository;
    private final PostLikeRepository postLikeRepository;
    private final PostMediaRepository postMediaRepository;
    private final UserRepository userRepository;
    private final TripRepository tripRepository;
    private final MediaAssetRepository mediaAssetRepository;
    private final UserService userService;

    @Transactional
    public PostRes createPost(Long currentUserId, CreatePostReq req) {
        // 필수 연관 엔티티 조회 및 유효성 검증
        User author = userRepository.findById(currentUserId)
                .orElseThrow(() -> new NoSuchElementException("유저를 찾을 수 없습니다. [ID: "+currentUserId+ "]"));
        Trip trip = tripRepository.findById(req.getTripId())
                .orElseThrow(() -> new NoSuchElementException("여행 정보를찾을 수 없습니다. [ID: "+req.getTripId()+"]"));

        // Post 엔티티 생성 및 저장
        Post post = req.toEntity(trip, author);
        Post savedPost = postRepository.save(post);

        List<PostMedia> savedPostMedias = new ArrayList<>();

        int position = 1;
        for (MediaAttachmentReq mediaReq : req.getMedia()){
            PostMedia postMedia = PostMedia.builder()
                    .post(savedPost)
                    .objectType(mediaReq.getObjectType())
                    .objectId(mediaReq.getObjectId())
                    .position(position++)
                    .build();
            PostMedia savedPostMedia = postMediaRepository.save(postMedia);
            savedPostMedias.add(savedPostMedia);
//            postMediaRepository.save(postMedia);
        }

        //미디어 에셋 정보 조회
        Set<Long> assetIds = savedPostMedias.stream()
                .map(PostMedia::getObjectId)
                .collect(Collectors.toSet());
        List<MediaAsset> mediaAssetList = mediaAssetRepository.findMediaAssetDetailByIdIn(assetIds);

        Map<Long, MediaAsset> assetMap = mediaAssetList.stream()
                .collect(Collectors.toMap(MediaAsset::getId, asset -> asset));

        AuthorRes authorRes = postMapper.toAuthorRes(author);
        List<MediaRes> mediaResList = postMapper.toMediaResList(savedPostMedias, assetMap);

        // PostMapper를 사용하여 PostRes DTO 생성 및 반환
        // 게시글 생성 직후에는 좋아요와 댓글이 없으므로 0, false를 전달합니다.
        return postMapper.toPostRes(
                savedPost,
                authorRes,
                mediaResList,
                0,          // likeCount
                0,          // commentCount
                false       // isLiked
        );

//        // 생성 직후 미디어 목록을 DTO로 변환
//        List<MediaRes> mediaResList = postMapper.toMediaResListForCreation(req.getMedia());
//        // 응답 STO 생성 및 반환
//        // Mapper 사용해 PostRes DTO 생성
//        return PostRes.builder()
//                .id(savedPost.getId())
//                .author(authorRes)
//                .caption(savedPost.getCaption())
//                .tripId(savedPost.getTrip().getId())
//                .visibility(savedPost.getVisibility().name())
//                .likeCount(0)
//                .commentCount(0)
//                .isLiked(false)
//                .media(mediaResList)
//                .createdAt(savedPost.getCreatedAt())
//                .build();
    }

    @Transactional
    public PostRes updatePost(Long postId,Long currentUserId, UpdatePostReq req) {
        // 게시글 조회 및 권한 확인
        Post post = postRepository.findById(postId)
                .orElseThrow(() -> new NoSuchElementException("게시글을 찾을 수 없습니다. [ID:" + postId + "]"));

        // 권한 확인: 게시글 작성자와 현재 유저 ID가 일치하는지 확인
        if (!post.getAuthor().getId().equals(currentUserId)) {
            // 권한 없음 예외 발생 (별도 Custom Exception 정의 필요)
            throw new IllegalStateException("게시글 수정 권한이 없습니다.");
        }

        // Post 엔티티 본문 수정
        // Post 엔티티에 update(caption, visibility) 메서드가 정의되어야 합니다.
        post.update(
                req.getCaption(),
                req.getVisibility()
        );

        // PostMedia 목록 교체
        // 기존 PostMedia 기록 모두 삭제
        postMediaRepository.deleteAllByPostId(postId);

        // 3-2. 새로운 PostMedia 목록 저장 (createPost와 동일한 로직)
        int position = 1;
        for (MediaAttachmentReq mediaReq : req.getMedia()) {
            PostMedia postMedia = PostMedia.builder()
                    .post(post)
                    .objectType(mediaReq.getObjectType())
                    .objectId(mediaReq.getObjectId())
                    .position(position++)
                    .build();
            postMediaRepository.save(postMedia);
        }

        // 응답 DTO 생성 (수정 후이므로 통계 값 재계산 또는 0으로 반환)
        // 수정 후의 응답이므로, PostRes를 반환
        // 좋아요/댓글 수는 0으로 임시 설정하거나, 간단한 쿼리로 조회해야 합니다.
        AuthorRes authorRes = postMapper.toAuthorRes(post.getAuthor());
        List<MediaRes> mediaResList = postMapper.toMediaResListForCreation(req.getMedia());

        return PostRes.builder()
                .id(post.getId())
                .author(authorRes)
                .caption(post.getCaption())
                .visibility(post.getVisibility().name())
                .media(mediaResList)
                .updatedAt(post.getUpdatedAt())
                .build();
    }

    @Transactional
    public void deletePost(Long postId, Long currentUserId) {

        // 1. 게시글 조회 및 권한 확인
        Post post = postRepository.findById(postId)
                .orElseThrow(() -> new NoSuchElementException("게시글을 찾을 수 없습니다. [ID:" + postId + "]"));

        if (!post.getAuthor().getId().equals(currentUserId)) {
            throw new IllegalStateException("게시글 삭제 권한이 없습니다.");
        }
        postMediaRepository.deleteAllByPostId(postId);
        // 게시글 삭제
        postRepository.delete(post);
    }

    @Transactional
    public boolean toggleLike(Long postId,Long currentUserId) {
        Post post = postRepository.findById(postId)
                .orElseThrow(() -> new NoSuchElementException("게시글을 찾을 수 없습니다. [ID:" + postId + "]"));
        User user = userRepository.findById(currentUserId)
                .orElseThrow(() -> new NoSuchElementException("유저를 찾을 수 없습니다. [ID:" + currentUserId + "]"));

        // 좋아요 기록 찾기
        Optional<Like> existingLike = postLikeRepository.findByPostIdAndUserId(postId, currentUserId);

        if (existingLike.isPresent()) {
            // 3. 기록이 존재하면 (이미 좋아요 상태) -> 삭제 (좋아요 취소)
            postLikeRepository.delete(existingLike.get());
            return false;

        } else {
            // 4. 기록이 없으면 -> 생성 (좋아요 추가)
            Like newLike = Like.builder()
                    .post(post)
                    .user(user)
                    .type("POST") // 좋아요 타입 (예: POST 등)
                    .build();

            postLikeRepository.save(newLike);
            return true;
        }
    }

    @Transactional
    public CommentRes createComment(Long postId, Long currentUserId, CreateCommentReq req) {
        User author = userRepository.findById(currentUserId)
                .orElseThrow(() -> new NoSuchElementException("유저를 찾을 수 없습니다. [ID:" + currentUserId + "]"));
        Post post = postRepository.findById(postId)
                .orElseThrow(() -> new NoSuchElementException("게시글을 찾을 수 없습니다. [ID:" + postId + "]"));

        Comment comment = req.toEntity(post, author);
        Comment savedComment = commentRepository.save(comment);

        AuthorRes authorRes = postMapper.toAuthorRes(author);

        return CommentRes.builder()
                .commenter(authorRes)
                .comment(savedComment.getContent())
                .createdAt(savedComment.getCreatedAt())
                .build();

    }

    @Transactional
    public void deleteComment(Long postId, Long commentId, Long currentUserId) {

        // 1. 댓글  조회 및 권한 확인
        Comment comment = commentRepository.findByPostIdAndId(postId, commentId)
                .orElseThrow(() -> new NoSuchElementException("댓글을 찾을 수 없습니다. [post ID:" + postId + "] [comment ID:" + commentId + "]"));

        if (!comment.getAuthor().getId().equals(currentUserId)) {
            throw new IllegalStateException("댓글 삭제 권한이 없습니다.");
        }
        // 댓글 삭제
        commentRepository.delete(comment);
    }

    @Transactional
    public CommentListRes getCommentList(Long postId, Long currentUserId) {
        User author = userRepository.findById(currentUserId)
                .orElseThrow(() -> new NoSuchElementException("유저를 찾을 수 없습니다. [ID:" + currentUserId + "]"));
        Post post = postRepository.findById(postId)
                .orElseThrow(() -> new NoSuchElementException("게시글을 찾을 수 없습니다. [ID:" + postId + "]"));

        List<Comment> comments = commentRepository.findByPostIdWithCommenter(postId);

        // 3. Comment 엔티티 리스트를 CommentRes DTO 리스트로 변환
        List<CommentRes> commentResList = comments.stream()
                // CommentRes DTO의 엔티티를 받는 생성자를 활용 (2번에서 가정한 방식)
                .map(CommentRes::new)
                .collect(Collectors.toList());

        // 4. CommentListRes로 감싸서 반환
        return CommentListRes.builder()
                .comments(commentResList)
                .build();
    }

    @Transactional
    public PostLocaListRes getPostLoca(Long currentUserId) {
        User author = userRepository.findById(currentUserId)
                .orElseThrow(() -> new NoSuchElementException("유저를 찾을 수 없습니다. [ID:" + currentUserId + "]"));

        // Repository의 DTO 조회 쿼리 호출
        // Post 엔티티를 거치지 않고 바로 PostsLocaRes DTO 리스트를 가져옴
        List<PostsLocaRes> postLocaList = postRepository.findAllPostLocaByAuthorId(currentUserId);

        // 3. PostLocaListRes DTO로 감싸서 반환
        return PostLocaListRes.builder()
                .posts(postLocaList)
                .build();

    }


    @Transactional
    public AllPostListRes getPostFeed(String feedType, Long targetUserId, LocalDateTime cursor, int limit, Long currentUserId) {

        // 1. Repository 호출: 다음 페이지 확인을 위해 limit + 1 요청
        List<Post> fetchedPosts = postRepository.findPostListWithFilteringAndCursor(
                feedType,
                targetUserId,
                cursor,
                limit + 1
        );

        // 2. 접근 제어 (Visibility) 필터링 (Repository에서 처리되지 않은 경우 Service에서 처리)
        List<Post> accessPosts = filterPostsByVisibility(fetchedPosts, targetUserId, currentUserId);

        // 3. 페이지네이션 처리
        // 필터링 후 limit + 1개를 초과하는지 확인해야 하지만, 성능을 위해 간단히 구현합니다.
        // (실제로는 커서 값을 다시 조정하는 복잡한 로직이 필요할 수 있음)
        List<Post> postsToProcess = accessPosts.size() > limit ? accessPosts.subList(0, limit) : accessPosts;
        boolean hasNext = accessPosts.size() > limit;

        // 4. 게시글 ID 리스트 추출 (Bulk 조회에 사용)
        List<Long> postIds = postsToProcess.stream()
                .map(Post::getId)
                .collect(Collectors.toList());

        if (postIds.isEmpty()) {
            return AllPostListRes.builder()
                    .posts(Collections.emptyList())
                    .nextCursor(null)
                    .hashNext(false)
                    .build();
        }

        // 5. Bulk 데이터 조회 (N+1 문제 방지)
        // a. 좋아요/댓글 수 조회
        Map<Long, Integer> likeCounts = postLikeRepository.findLikeCountsByPostIds(postIds);
        Map<Long, Integer> commentCounts = commentRepository.findCommentCountsByPostIds(postIds);

        // b. 현재 유저의 좋아요 여부 조회
        Set<Long> likedPostIds = postLikeRepository.findLikedPostIdsByUserIdAndPostIds(currentUserId, postIds);

        // c. 미디어 정보 일괄 조회
        List<PostMedia> postMediaList = postMediaRepository.findByPostIdIn(postIds);
        Set<Long> assetIds = postMediaList.stream().map(yeohaenggasijo.tripshot.domain.post.PostMedia::getObjectId).collect(Collectors.toSet());
        List<MediaAsset> mediaAssetList = mediaAssetRepository.findMediaAssetDetailByIdIn(assetIds);

        Map<Long, MediaAsset> assetMap = mediaAssetList.stream()
                .collect(Collectors.toMap(
                        MediaAsset::getId, // Key: MediaAsset의 ID
                        asset -> asset    // Value: MediaAsset 엔티티 자체
                ));

        // 6. DTO 변환 및 데이터 통합
        List<PostRes> postResList = postsToProcess.stream()
                .map(post -> {
                    // a. 작성자 정보 변환
                    AuthorRes authorRes = postMapper.toAuthorRes(post.getAuthor());

                    // b. 미디어 정보 변환
                    List<PostMedia> postMediasForPost = postMediaList.stream()
                            .filter(pm -> pm.getPost().getId().equals(post.getId()))
                            .collect(Collectors.toList());
                    List<MediaRes> mediaList = postMapper.toMediaResList(postMediasForPost, assetMap);

                    // c. 최종 DTO 생성
                    return postMapper.toPostRes(
                            post,
                            authorRes,
                            mediaList,
                            likeCounts.getOrDefault(post.getId(), 0),
                            commentCounts.getOrDefault(post.getId(), 0),
                            likedPostIds.contains(post.getId())
                    );
                })
                .collect(Collectors.toList());

        // 7. 다음 커서 값 설정
        LocalDateTime nextCursor = null;
        if (hasNext && !postResList.isEmpty()) {
            nextCursor = postResList.get(postResList.size() - 1).getCreatedAt();
        }

        // 8. 최종 DTO 반환
        return AllPostListRes.builder()
                .posts(postResList)
                .nextCursor(nextCursor)
                .hashNext(hasNext) // DTO 필드명이 hashNext인 것에 주의
                .build();
    }

    //TODO: 이거 리팩토링 해야할거같긴함..
    private List<Post> filterPostsByVisibility(List<Post> posts, Long targetUserId, Long currentUserId) {
        if (currentUserId == null) {
            // 비로그인 사용자는 PUBLIC만 볼 수 있습니다.
            return posts.stream()
                    .filter(post -> post.getVisibility().name().equals("PUBLIC"))
                    .collect(Collectors.toList());
        }

        return posts.stream().filter(post -> {
            // 1. 내 게시글은 무조건 접근 가능
            if (post.getAuthor().getId().equals(currentUserId)) {
                return true;
            }

            // 3. PRIVATE은 다른 사람이면 접근 불가능
            if (post.getVisibility().name().equals("PRIVATE")) {
                return false;
            }

            // 4. FRIEND_ONLY: 현재 유저가 작성자의 친구인지 확인해야 합니다.
            if (post.getVisibility().name().equals("FRIENDS")) {
                // isFriend() 메서드가 UserpService에 정의
                return userService.isFriend(currentUserId, post.getAuthor().getId());
            }

            return false;
        }).collect(Collectors.toList());
    }

}
