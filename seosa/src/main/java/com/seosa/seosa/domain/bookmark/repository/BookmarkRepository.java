package com.seosa.seosa.domain.bookmark.repository;

import com.seosa.seosa.domain.bookmark.entity.Bookmark;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface BookmarkRepository extends JpaRepository<Bookmark , Long> , BookmarkRepositoryCustom {

    @Query("select b from Bookmark  b where b.user.userId =:userId")
    List<Bookmark> findByUserId(@Param("userId") Long userId);

    @Query("select count(b) > 0 from Bookmark b where b.user.userId= :userId and b.post.postId = :postId")
    boolean existsByUserIdAndPostId(@Param("userId") Long userId , @Param("postId") Long postId);

    @Query("select b from Bookmark b where b.user.userId= :userId and b.post.postId= :postId")
    Optional<Bookmark> findByUserIdAndPostId(@Param("userId") Long userId , @Param("postId") Long postId);

}
