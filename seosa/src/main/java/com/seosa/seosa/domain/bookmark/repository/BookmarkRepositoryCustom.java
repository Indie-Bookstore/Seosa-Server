package com.seosa.seosa.domain.bookmark.repository;

import com.seosa.seosa.domain.bookmark.dto.Response.BookmarkResDto;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface BookmarkRepositoryCustom {

    Page<BookmarkResDto> findMyBookmarksWithCursor(Long userId, String customCursor, Pageable pageable);
}
