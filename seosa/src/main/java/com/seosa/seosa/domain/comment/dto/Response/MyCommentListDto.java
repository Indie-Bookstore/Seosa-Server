package com.seosa.seosa.domain.comment.dto.Response;

import com.seosa.seosa.domain.post.dto.Response.PostSimpleResDto;
import io.swagger.v3.oas.annotations.media.Schema;

import java.util.List;

public record MyCommentListDto(
        @Schema(description = "내가 댓 댓글 최신순 9개씩")
        List<MyCommentDetailDto> comments,
        @Schema(description = "다음 커서로 사용될 postId")
        int cursorId ,
        @Schema(description = "다음 페이지 존재 여부")
        boolean hasNext
){
}
