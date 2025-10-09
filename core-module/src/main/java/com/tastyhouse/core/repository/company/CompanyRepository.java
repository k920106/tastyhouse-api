package com.tastyhouse.core.repository.company;

import com.tastyhouse.core.entity.company.Company;

import java.util.List;
import java.util.Optional;

public interface CompanyRepository {
    List<Company> findAll();
    Optional<Company> findById(Long id);
}
