package com.seosa.seosa.domain.bookmark.repository;

import com.seosa.seosa.domain.bookmark.dto.Response.BookmarkResDto;
import com.seosa.seosa.domain.post.dto.Response.PostCursorDto;
import com.seosa.seosa.domain.post.dto.Response.PostSimpleResDto;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface BookmarkRepositoryCustom {

    PostCursorDto findMyBookmarksWithCursor(Long userId, String customCursor, Pageable pageable);
}
