package com.seosa.seosa.domain.bookstore.repository;

import com.seosa.seosa.domain.bookstore.entity.Bookstore;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface BookstoreRepository extends JpaRepository<Bookstore , Long> {

    @Query("select b from Bookstore b where b.bookstoreId =:bookstoreId")
    Optional<Bookstore> findByBookstoreId(@Param("bookstoreId")Long bookstoreId);
}
