package com.seosa.seosa.domain.post.dto.Response;

import com.seosa.seosa.domain.bookmark.entity.Bookmark;
import com.seosa.seosa.domain.comment.entity.Comment;
import com.seosa.seosa.domain.post.entity.Post;
import io.swagger.v3.oas.annotations.media.Schema;

import java.time.LocalDateTime;

public record PostSimpleResDto(

        @Schema(description = "글 id")
        Long postId,

        @Schema(description = "글 제목")
        String title,

        @Schema(description = "썸네일 url")
        String thumbnailUrl,

        @Schema(description = "글 작성자 id")
        Long userId,

        @Schema(description = "글 작성자 이름")
        String userName,

        @Schema(description = "글 생성날짜")
        LocalDateTime createdAt,

        @Schema(description = "글의 북마크 수")
        int bookmarkCount,

        @Schema(description = "글의 댓글 수")
        int commentCount

) {

    public  static PostSimpleResDto to(Post post){

          return new PostSimpleResDto(
                post.getPostId(),
                  post.getTitle(),
                  post.getThumbnailUrl(),
                  post.getUser().getUserId(),
                  post.getUser().getNickname(),
                  post.getCreatedAt(),
                  post.getBookmarks().size(),
                  post.getComments().size()
          );
    }

    public static PostSimpleResDto from(Bookmark bookmark){
        Post post = bookmark.getPost();
        return new PostSimpleResDto(
                post.getPostId(),
                post.getTitle(),
                post.getThumbnailUrl(),
                post.getUser().getUserId(),
                post.getUser().getNickname(),
                post.getCreatedAt(),
                post.getBookmarks().size(),
                post.getComments().size()
        );
    }

    public static PostSimpleResDto from (Comment comment){
        Post post = comment.getPost();
        return new PostSimpleResDto(
                post.getPostId(),
                post.getTitle(),
                post.getThumbnailUrl(),
                post.getUser().getUserId(),
                post.getUser().getNickname(),
                post.getCreatedAt(),
                post.getBookmarks().size(),
                post.getComments().size()
        );
    }
 }
