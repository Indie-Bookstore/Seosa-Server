package com.seosa.seosa.domain.post.dto.Response;

import com.seosa.seosa.domain.product.entity.Product;
import io.swagger.v3.oas.annotations.media.Schema;

public record ProductResDto(
        @Schema(description = "제품 id")
        Long productId,
        @Schema(description = "제품 제목")
    String productName,
                @Schema(description = "제품 가격")
        int price,
        @Schema(description = "제품 이미지 url")
       String productImg,
        @Schema(description = "제품의 간단한 설명")
      String description
) {

    public static ProductResDto to(Product product){
        return new ProductResDto(
                product.getProductId(),
                product.getProductName(),
                product.getPrice(),
                product.getProductImg(),
                product.getDescription()
        );
    }
}
