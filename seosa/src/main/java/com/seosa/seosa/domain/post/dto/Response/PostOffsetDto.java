package com.seosa.seosa.domain.post.dto.Response;

import org.springframework.data.domain.Page;

import java.util.List;

public record PostOffsetDto(
        List<PostSimpleResDto> posts,
        int pageNumber,
        int pageSize,
        long totalElements,
        boolean hasNext
) {
    public static PostOffsetDto from(Page<PostSimpleResDto> page) {
        return new PostOffsetDto(
                page.getContent(),
                page.getNumber(),
                page.getSize(),
                page.getTotalElements(),
                page.hasNext()
        );
    }
}