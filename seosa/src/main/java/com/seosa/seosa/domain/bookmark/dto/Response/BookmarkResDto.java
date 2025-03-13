package com.seosa.seosa.domain.bookmark.dto.Response;

import com.seosa.seosa.domain.bookmark.entity.Bookmark;
import com.seosa.seosa.domain.post.entity.Post;
import com.seosa.seosa.domain.user.entity.User;
import io.swagger.v3.oas.annotations.media.Schema;

import java.time.LocalDateTime;

public record BookmarkResDto (

        @Schema(description = "사용자 id")
        Long userId,

        @Schema(description = "사용자 이름")
        String name,

        @Schema(description = "글 id")
        Long postId,

        @Schema(description = "북마크 id")
        Long bookmarkId,

        @Schema(description = "생성 시간")
        LocalDateTime createdAt ,
        @Schema(description = "수정 시간")
        LocalDateTime updatedAt

){
    public static BookmarkResDto to(Bookmark bookmark){
        return new BookmarkResDto(
                bookmark.getUser().getUserId(),
                bookmark.getUser().getNickname(),
                bookmark.getPost().getPostId(),
                bookmark.getBookmarkId(),
                bookmark.getCreatedAt(),
                bookmark.getUpdatedAt()
        );
    }


}
