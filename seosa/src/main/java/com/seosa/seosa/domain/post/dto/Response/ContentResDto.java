package com.seosa.seosa.domain.post.dto.Response;

import com.seosa.seosa.domain.content.entity.Content;
import com.seosa.seosa.domain.content.entity.ContentType;
import io.swagger.v3.oas.annotations.media.Schema;

public record ContentResDto(
        @Schema(description = "컨텐츠 id")
        Long contentId,
        @Schema(description = "컨텐츠 타입" , example = "sentence or img_url")
        ContentType contentType,
                @Schema(description = "컨텐츠 내용" , example = "글이나 이미지 url")
                        String content,
                @Schema(description = "컨텐츠 순서" , example = "0")
        int order_index
) {

    public static ContentResDto to(Content content){
        return  new ContentResDto(
                content.getContentId(),
                content.getContentType(),
                content.getBody(),
                content.getOrder_index()
        );
    }

}
