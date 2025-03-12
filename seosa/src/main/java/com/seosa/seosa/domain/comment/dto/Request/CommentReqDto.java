package com.seosa.seosa.domain.comment.dto.Request;

import com.seosa.seosa.domain.comment.dto.Response.CommentResDto;
import com.seosa.seosa.domain.comment.entity.Comment;
import com.seosa.seosa.domain.post.entity.Post;
import com.seosa.seosa.domain.user.entity.User;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class CommentReqDto {

    @Schema(description = "댓글")
    private String text;

    public CommentReqDto (String text){
        this.text = text;
    }

    public static Comment toEntity(CommentReqDto commentReqDto , Post post , User user){
        return Comment.builder()
                .text(commentReqDto.getText())
                .post(post)
                .user(user)
                .build();
    }
}
