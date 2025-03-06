package com.seosa.seosa.domain.product.repository;

import com.seosa.seosa.domain.product.entity.Product;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ProductRepository extends JpaRepository<Product , Long> {
    @Query("select p from Product  p where p.bookstore.bookstoreId =:bookstoreId")
    List<Product> findByBookstoreId(@Param("bookstoreId") Long bookstoreId);
}
