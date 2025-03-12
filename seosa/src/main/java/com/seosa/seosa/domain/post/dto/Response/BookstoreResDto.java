package com.seosa.seosa.domain.post.dto.Response;

import com.seosa.seosa.domain.bookstore.entity.Bookstore;
import com.seosa.seosa.domain.post.dto.Request.BookstoreReqDto;
import io.swagger.v3.oas.annotations.media.Schema;

public record BookstoreResDto(

        @Schema(description = "서점 id")
        Long bookstoreId,

        @Schema(description = "우편번호")
       String postalCode,
        @Schema(description = "주소(구까지)")
        String address,
        @Schema(description = "상세주소")
        String detailedAddress,
        @Schema(description = "운영 요일" , example = "월,화,수")
        String openDays,
        @Schema(description = "운영 시간" , example = "9:00~18:00")
        String openHours,
        @Schema(description = "전화번호")
        String phoneNumber,
        @Schema(description = "인스타그램 주소")
        String instagramLink

) {

    public static BookstoreResDto to(Bookstore bookstore){
        return new BookstoreResDto(
                bookstore.getBookstoreId(),
                bookstore.getPostalcode(),
                bookstore.getAddress(),
                bookstore.getDetailedAddress(),
                bookstore.getOpenDays(),
                bookstore.getOpenHours(),
                bookstore.getPhoneNumber(),
                bookstore.getInstagramLink()
        );
    }
}
