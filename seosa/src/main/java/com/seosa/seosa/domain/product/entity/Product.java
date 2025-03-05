package com.seosa.seosa.domain.product.entity;

import com.seosa.seosa.domain.bookstore.entity.Bookstore;
import com.seosa.seosa.domain.post.entity.Post;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Product {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long productId;

    @Column(length = 100 , nullable = false)
    private String productName;

    @Column(nullable = false)
    private int price;

    @Column(length = 100)
    private String productImg;

    @Column(length = 200)
    private String manual;

    @ManyToOne
    @JoinColumn(name = "bookstore_id")
    private Bookstore bookstore;

    @ManyToOne
    @JoinColumn(name = "post_id")
    private Post post;

    @Builder
    public Product(String productName , int price , String productImg , String manual){
        this.productName = productName;
        this.price = price;
        this.productImg = productImg;
        this.manual = manual;
    }
}
