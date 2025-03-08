package com.seosa.seosa.domain.post.entity;

import com.seosa.seosa.domain.bookstore.entity.Bookstore;
import com.seosa.seosa.domain.comment.entity.Comment;
import com.seosa.seosa.domain.content.entity.Content;
import com.seosa.seosa.domain.user.entity.User;
import com.seosa.seosa.global.entity.BaseTimeEntity;
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
public class Post extends BaseTimeEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long postId;

    @Column(length = 100)
    private String title;

    @Column(length = 100)
    private String location;

    @ManyToOne
    @JoinColumn(name = "user_id")
    private User user;

    @ManyToOne
    @JoinColumn(name = "bookstore_id")
    private Bookstore bookstore;

    @OneToMany(mappedBy = "post", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Comment> comments = new ArrayList<>();

    @OneToMany(mappedBy = "post", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Content> contents = new ArrayList<>();

    @Builder
    public Post(String title , String location , Bookstore bookstore , User user){
        this.title = title;
        this.location = location;
        this.bookstore = bookstore;
        this.user = user;
    }


}
