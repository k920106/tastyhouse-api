package com.tastyhouse.core.repository.faq;

import com.tastyhouse.core.entity.faq.Faq;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface FaqJpaRepository extends JpaRepository<Faq, Long> {
}
