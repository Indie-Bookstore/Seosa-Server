package com.seosa.seosa.domain.comment.repository;

import com.seosa.seosa.domain.comment.dto.Response.MyCommentListDto;
import org.springframework.data.domain.Pageable;

public interface CommentRepositoryCustom {

    MyCommentListDto findMyCommentsWithCursor(Long userId, String customCursor, Pageable pageable);
}
