package com.seosa.seosa.domain.bookmark.dto.Response;

import io.swagger.v3.oas.annotations.media.Schema;

import java.util.List;

public record BookmarkCursorDto(
        @Schema(description = "내 북마크 목록 중 9개")
        List<BookmarkResDto> bookmarks,
        @Schema(description = "다음 커서로 사용될 bookmarkId")
        int cursorId ,
        @Schema(description = "다음 페이지 존재 여부")
        boolean hasNext
) {
}
