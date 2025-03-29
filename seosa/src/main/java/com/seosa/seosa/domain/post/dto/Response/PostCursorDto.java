package com.seosa.seosa.domain.post.dto.Response;

import io.swagger.v3.oas.annotations.media.Schema;

import java.util.List;

public record PostCursorDto(

        @Schema(description = "게시물 최신순 5개씩")
        List<PostSimpleResDto> posts,
        @Schema(description = "다음 커서로 사용될 postId")
        int cursorId ,
        @Schema(description = "다음 페이지 존재 여부")
        boolean hasNext
) {
}
