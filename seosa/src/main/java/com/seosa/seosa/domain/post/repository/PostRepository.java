package com.seosa.seosa.domain.post.repository;

import com.seosa.seosa.domain.post.entity.Post;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Optional;

public interface PostRepository extends JpaRepository<Post, Long> {

    @Query("select p from Post  p where p.user.userId =:userId AND p.postId =:postId")
    Optional<Post> findBypostIdAnduserId(@Param("postId") Long postId, @Param("userId") Long userId);
}
