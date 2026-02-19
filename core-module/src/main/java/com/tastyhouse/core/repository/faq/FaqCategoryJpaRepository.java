package com.tastyhouse.core.repository.faq;

import com.tastyhouse.core.entity.faq.FaqCategory;
import org.springframework.data.jpa.repository.JpaRepository;

public interface FaqCategoryJpaRepository extends JpaRepository<FaqCategory, Long> {
}
