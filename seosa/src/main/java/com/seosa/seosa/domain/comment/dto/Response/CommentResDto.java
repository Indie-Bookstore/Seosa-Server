package com.seosa.seosa.domain.comment.dto.Response;

import com.seosa.seosa.domain.comment.entity.Comment;
import com.seosa.seosa.domain.post.entity.Post;
import com.seosa.seosa.domain.user.entity.User;
import io.swagger.v3.oas.annotations.media.Schema;

import java.time.LocalDateTime;

public record CommentResDto (
        @Schema(description = "사용자 id")
        Long userId,
        @Schema(description = "사용자 이름")
        String name,
        @Schema(description = "사용자 프로필 이미지 url")
        String profileImgUrl,
        @Schema(description = "댓글 id")
        Long commentId ,
        @Schema(description = "댓글 내용")
        String text,
        @Schema(description = "생성 시간")
        LocalDateTime createdAt ,
        @Schema(description = "수정 시간")
        LocalDateTime updatedAt,
        @Schema(description = "글 id")
        Long postId

){

    public static CommentResDto to(Comment comment , User user , Post post){
        return new CommentResDto(
                user.getUserId(),
                user.getNickname(),
                user.getProfileImage(),
                comment.getCommentId(),
                comment.getText(),
                comment.getCreatedAt() ,
                comment.getUpdatedAt(),
                post.getPostId()
        );
    }
}
