package com.seosa.seosa.domain.content.entity;

import com.seosa.seosa.domain.post.entity.Post;
import com.seosa.seosa.domain.user.entity.User;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Content {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long contentId;

    @Column(nullable = false)
    @Enumerated(EnumType.STRING)
    private ContentType contentType;

    @Column(length = 100)
    private String body;

    @Column
    private int order_index;

    @ManyToOne
    @JoinColumn(name = "post_id")
    private Post post;

    @Builder
    public Content(String body , int order_index){
        this.body = body;
        this.order_index = order_index;
    }

}
