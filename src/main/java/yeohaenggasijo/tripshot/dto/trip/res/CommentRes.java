package yeohaenggasijo.tripshot.dto.trip.res;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import yeohaenggasijo.tripshot.domain.post.Comment;

import java.time.LocalDateTime;

@Getter
@Builder
@AllArgsConstructor
public class CommentRes {
    private  final Long commentId;
    private final AuthorRes commenter;
    private final String comment;
    @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss'Z'")
    private final LocalDateTime createdAt;

    public CommentRes(Comment comment) {
        this.commentId = comment.getId();
        // 1. 작성자(User) 엔티티를 AuthorRes DTO로 변환
        this.commenter = new AuthorRes(comment.getAuthor());

        // 2. 댓글 내용 매핑 (Comment 엔티티의 필드명이 'content'라고 가정)
        this.comment = comment.getContent();

        // 3. 생성 시간 매핑
        this.createdAt = comment.getCreatedAt();
    }
}
