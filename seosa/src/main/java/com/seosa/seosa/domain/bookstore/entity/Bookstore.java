package com.seosa.seosa.domain.bookstore.entity;

import com.seosa.seosa.domain.comment.entity.Comment;
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

    @Column(length = 100)
    private String openHours;

    @Column(length = 50)
    private String openDays;

    @Column(length = 50)
    private String phoneNumber;

    @Column(length = 200)
    private String IG_Address;

    @OneToMany(mappedBy = "bookstore", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Post> posts = new ArrayList<>();

    @OneToMany(mappedBy = "bookstore", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Product> products = new ArrayList<>();


    @Builder
    public Bookstore (String address , String detailedAddress , String postalcode , String openDays , String openHours , String phoneNumber , String IG_Address){
        this.address = address;
        this.detailedAddress = detailedAddress;
        this.postalcode = postalcode;
        this.openDays = openDays;
        this.openHours = openHours;
        this.phoneNumber = phoneNumber;
        this.IG_Address = IG_Address;
    }
}
