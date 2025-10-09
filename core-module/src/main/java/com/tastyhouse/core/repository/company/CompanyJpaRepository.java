package com.tastyhouse.core.repository.company;

import com.tastyhouse.core.entity.company.Company;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CompanyJpaRepository extends JpaRepository<Company, Long> {
}
