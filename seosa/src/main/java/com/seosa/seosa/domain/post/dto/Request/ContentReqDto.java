package com.seosa.seosa.domain.post.dto.Request;

import com.seosa.seosa.domain.content.entity.Content;
import com.seosa.seosa.domain.content.entity.ContentType;
import com.seosa.seosa.domain.post.entity.Post;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class ContentReqDto {

    @Schema(description = "컨텐츠 타입" , example = "sentence , img_url")
    private ContentType contentType;
    @Schema(description = "컨텐츠 내용" , example = "글이나 이미지 url")
    private String content;
    @Schema(description = "컨텐츠 순서" , example = "0")
    private int order_index;

    public ContentReqDto(ContentType contentType , String content , int order_index){
        this.contentType = contentType;
        this.content = content;
        this.order_index = order_index;
    }

    public static Content toEntity(ContentReqDto contentReqDto , Post post){
        return Content.builder()
                .contentType(contentReqDto.getContentType())
                .body(contentReqDto.getContent())
                .order_index(contentReqDto.getOrder_index())
                .post(post)
                .build();
    }



}
