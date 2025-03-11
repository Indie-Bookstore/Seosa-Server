package com.seosa.seosa.domain.faq.repository;

import com.seosa.seosa.domain.faq.entity.FAQ;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface FAQRepository extends JpaRepository<FAQ, Long> {
}

