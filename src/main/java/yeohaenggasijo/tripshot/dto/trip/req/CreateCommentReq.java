package yeohaenggasijo.tripshot.dto.trip.req;

import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import yeohaenggasijo.tripshot.domain.post.Comment;
import yeohaenggasijo.tripshot.domain.post.Post;
import yeohaenggasijo.tripshot.domain.user.User;

@Getter
public class CreateCommentReq {
    @NotBlank(message = "댓글 내용은 필수입니다.")
    private String comment;

    public Comment toEntity(Post post, User author) {
        return Comment.builder()
                .content(this.comment) // DTO의 필드 사용
                .post(post)            // 인자로 받은 Post 엔티티
                .author(author)        // 인자로 받은 User(Author) 엔티티
                .build();
    }
}
