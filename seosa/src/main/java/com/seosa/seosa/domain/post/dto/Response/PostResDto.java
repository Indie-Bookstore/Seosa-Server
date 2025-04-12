package com.seosa.seosa.domain.post.dto.Response;


import com.seosa.seosa.domain.post.entity.Post;
import io.swagger.v3.oas.annotations.media.Schema;

import java.time.format.DateTimeFormatter;
import java.util.List;

public record PostResDto(

        @Schema(description = "글 id")
        Long postId,

        @Schema(description = "글 제목")
       String title,
        @Schema(description = "위치")
        String location,
        @Schema(description = "썸네일 url")
        String thumbnailUrl,

        @Schema(description = "게시 날짜")
        String createdAt,

        @Schema(description = "서점 정보")
       BookstoreResDto bookstoreResDto,
        @Schema(description = "컨텐츠 리스트")
        List<ContentResDto> contentResDtoList,
        @Schema(description = "서점 자체 상품 리스트")
     List<ProductResDto> productResDtoList

) {

    public static PostResDto to(Post post , BookstoreResDto bookstoreResDto ,List<ContentResDto> contentResDtos , List<ProductResDto> productResDtos){


        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm");
        String formattedCreatedAt = post.getCreatedAt().format(formatter);


        return new PostResDto(
                post.getPostId(),
                post.getTitle(),
                post.getLocation(),
                post.getThumbnailUrl(),
                formattedCreatedAt,
                bookstoreResDto,
                contentResDtos,
                productResDtos
        );
    }
}
