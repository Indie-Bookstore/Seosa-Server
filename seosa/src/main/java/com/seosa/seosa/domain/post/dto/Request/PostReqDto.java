package com.seosa.seosa.domain.post.dto.Request;

import com.seosa.seosa.domain.bookstore.entity.Bookstore;
import com.seosa.seosa.domain.post.entity.Post;
import com.seosa.seosa.domain.user.entity.User;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.List;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class PostReqDto {

    @Schema(description = "글 제목")
    private String title;
    @Schema(description = "위치")
    private String location;
    @Schema(description = "글의 썸네일")
    private String thumbnailUrl;
    @Schema(description = "서점 정보")
    private BookstoreReqDto bookstoreReqDto;
    @Schema(description = "컨텐츠 리스트")
    private List<ContentReqDto> contentReqDtoList;
    @Schema(description = "서점 자체 상품 리스트")
    private List<ProductReqDto> productReqDtoList;

    public PostReqDto(String title , String location  , String thumbnailUrl , BookstoreReqDto bookstoreReqDto , List<ContentReqDto> contentReqDtoList, List<ProductReqDto> productReqDtoList){
        this.title = title;
        this.location = location;
        this.thumbnailUrl = thumbnailUrl;
        this.bookstoreReqDto = bookstoreReqDto;
        this.contentReqDtoList = contentReqDtoList;
        this.productReqDtoList = productReqDtoList;
    }

    public static Post toEntity(PostReqDto postReqDto , Bookstore bookstore , User user){
        return Post.builder()
                .title(postReqDto.getTitle())
                .location(postReqDto.getLocation())
                .thumbnailUrl(postReqDto.thumbnailUrl)
                .bookstore(bookstore)
                .user(user)
                .build();
    }
}
