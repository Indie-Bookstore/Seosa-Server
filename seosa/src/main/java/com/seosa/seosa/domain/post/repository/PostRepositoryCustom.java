package com.seosa.seosa.domain.post.repository;


import com.seosa.seosa.domain.post.dto.Response.PostSimpleResDto;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface PostRepositoryCustom {

    Page<PostSimpleResDto> findMainPostsWithCursor(String customCursor, Pageable pageable);

    Page<PostSimpleResDto> findMyPostsWithCursor(Long userId ,String customCursor, Pageable pageable);

    Page<PostSimpleResDto> findAllPostsWithOffset(Pageable pageable);
}
