package com.seosa.seosa.domain.comment.repository;

import com.seosa.seosa.domain.comment.entity.Comment;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface CommentRepository extends JpaRepository< Comment , Long> {
    @Query("select c from Comment  c where c.commentId =:commentId")
    Optional<Comment> findByCommentId(@Param("commentId")Long commentId);

    @Query("select c from Comment  c where c.post.postId =:postId")
    List<Comment> findByPostId(@Param("postId")Long postId);

    @Query("select c from Comment c where c.user.userId = :userId order by c.createdAt desc")
    List<Comment> findByUserId(@Param("userId") Long userId);
}
