package com.seosa.seosa.domain.content.repository;

import com.seosa.seosa.domain.content.entity.Content;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ContentRepository extends JpaRepository<Content , Long> {

    @Query("SELECT c FROM Content c WHERE c.post.postId =:postId")
    List<Content> findByPostId(@Param("postId") Long postId);
}
