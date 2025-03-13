package com.seosa.seosa.domain.bookmark.repository;

import com.seosa.seosa.domain.bookmark.entity.Bookmark;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface BookmarkRepository extends JpaRepository<Bookmark , Long> {

    @Query("select b from Bookmark  b where b.user.userId =:userId")
    List<Bookmark> findByUserId(@Param("userId") Long userId);
}
