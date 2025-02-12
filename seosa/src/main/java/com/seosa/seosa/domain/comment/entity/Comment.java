package com.seosa.seosa.domain.comment.entity;

import com.seosa.seosa.domain.post.entity.Post;
import com.seosa.seosa.domain.user.entity.User;
import com.seosa.seosa.global.entity.BaseTimeEntity;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Comment extends BaseTimeEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long commentId;

    @Column(length = 1024)
    private String comment;

    @ManyToOne
    @JoinColumn(name = "post_id")
    private Post post;

    @Builder
    public Comment(String comment){
        this.comment = comment;
    }

}
