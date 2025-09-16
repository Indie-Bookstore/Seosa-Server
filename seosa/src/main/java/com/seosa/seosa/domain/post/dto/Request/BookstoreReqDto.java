package com.seosa.seosa.domain.post.dto.Request;

import com.seosa.seosa.domain.bookstore.entity.Bookstore;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.persistence.Column;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class BookstoreReqDto {

    @Schema(description = "우편번호")
    private String postalCode;
    @Schema(description = "주소(구까지)")
    private String address;
    @Schema(description = "상세주소")
    private String detailedAddress;
    @Schema(description = "위도")
    private double lat;
    @Schema(description = "경도")
    private double lng;
    @Schema(description= "카카오 place id")
    private String kakaoPlaceId;
    @Schema(description = "운영 요일" , example = "월,화,수")
    private String openDays;
    @Schema(description = "운영 시간" , example = "9:00~18:00")
    private String openHours;
    @Schema(description = "전화번호")
    private String phoneNumber;
    @Schema(description = "인스타그램 주소")
    private String instagramLink;


    public BookstoreReqDto(String postalCode , String address , String detailedAddress , double lat , double lng , String kakaoPlaceId , String openDays,
                           String openHours , String phoneNumber , String instagramLink){
        this.postalCode = postalCode;
        this.address = address;
        this.detailedAddress = detailedAddress;
        this.lat = lat;
        this.lng = lng;
        this.kakaoPlaceId = kakaoPlaceId;
        this.openDays = openDays;
        this.openHours = openHours;
        this.phoneNumber = phoneNumber;
        this.instagramLink = instagramLink;
    }

    public static Bookstore toEntity(PostReqDto postReqDto){
        return Bookstore.builder()
                .postalcode(postReqDto.getBookstoreReqDto().postalCode)
                .address(postReqDto.getBookstoreReqDto().address)
                .detailedAddress(postReqDto.getBookstoreReqDto().detailedAddress)
                .lat(postReqDto.getBookstoreReqDto().lat)
                .lng(postReqDto.getBookstoreReqDto().lng)
                .kakaoPlaceId(postReqDto.getBookstoreReqDto().kakaoPlaceId)
                .phoneNumber(postReqDto.getBookstoreReqDto().phoneNumber)
                .openDays(postReqDto.getBookstoreReqDto().openDays)
                .openHours(postReqDto.getBookstoreReqDto().openHours)
                .instagramLink(postReqDto.getBookstoreReqDto().instagramLink)
                .build();
    }


}
