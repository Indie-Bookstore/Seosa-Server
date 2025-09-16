package com.seosa.seosa.domain.bookstore.entity;

import com.seosa.seosa.domain.post.entity.Post;
import com.seosa.seosa.domain.product.entity.Product;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.List;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Bookstore {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long bookstoreId;

    @Column(length = 100)
    private String address;

    @Column(length = 100)
    private String detailedAddress;

    @Column(length = 20)
    private String postalcode;

    @Column
    private double lat; // 위도

    @Column
    private double lng; // 경도

    @Column(name = "kakao_place_id", length = 50)
    private String kakaoPlaceId;

    @Column(length = 100)
    private String openHours;

    @Column(length = 50)
    private String openDays;

    @Column(length = 50)
    private String phoneNumber;

    @Column(length = 200)
    private String instagramLink;

    @OneToMany(mappedBy = "bookstore", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Post> posts = new ArrayList<>();

    @OneToMany(mappedBy = "bookstore", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Product> products = new ArrayList<>();


    @Builder
    public Bookstore (String address , String detailedAddress , String postalcode ,double lat , double lng , String kakaoPlaceId ,String openDays , String openHours , String phoneNumber , String instagramLink){
        this.address = address;
        this.detailedAddress = detailedAddress;
        this.postalcode = postalcode;
        this.lat = lat;
        this.lng = lng;
        this.kakaoPlaceId = kakaoPlaceId;
        this.openDays = openDays;
        this.openHours = openHours;
        this.phoneNumber = phoneNumber;
        this.instagramLink = instagramLink;
    }
}
