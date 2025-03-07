package com.seosa.seosa.domain.post.dto.Request;

import com.seosa.seosa.domain.bookstore.entity.Bookstore;
import com.seosa.seosa.domain.product.entity.Product;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class ProductReqDto {

    @Schema(description = "제품 제목")
    private String productName;
    @Schema(description = "제품 가격")
    private int price;
    @Schema(description = "제품 이미지 url")
    private String productImg;
    @Schema(description = "제품의 간단한 설명")
    private String description;

    public ProductReqDto(String productName , int price , String productImg , String description){
        this.productName = productName;
        this.price = price;
        this.productImg = productImg;
        this.description = description;
    }

    public static Product toEntity(ProductReqDto productReqDto , Bookstore bookstore){
        return Product.builder()
                .productName(productReqDto.getProductName())
                .price(productReqDto.getPrice())
                .productImg(productReqDto.getProductImg())
                .description(productReqDto.getDescription())
                .bookstore(bookstore)
                .build();
    }

}
