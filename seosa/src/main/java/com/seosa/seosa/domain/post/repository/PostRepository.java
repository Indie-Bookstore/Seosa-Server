package com.seosa.seosa.domain.post.repository;

import com.seosa.seosa.domain.post.entity.Post;
import org.springframework.data.jpa.repository.JpaRepository;

public interface PostRepository extends JpaRepository<Post, Long> {
}
