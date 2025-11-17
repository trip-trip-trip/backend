package yeohaenggasijo.tripshot.repository;

import yeohaenggasijo.tripshot.domain.post.Comment;

import java.util.List;

public interface CommentRepositoryCustom {
    // 특정 게시글의 댓글 목록을 작성자 정보와 함께 조회 (N+1 방지)
    List<Comment> findCommentsByPostIdWithAuthor(Long postId);
}
