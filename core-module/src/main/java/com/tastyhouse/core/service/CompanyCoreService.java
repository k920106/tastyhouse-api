package com.tastyhouse.core.service;

import com.tastyhouse.core.entity.company.Company;
import com.tastyhouse.core.repository.company.CompanyRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class CompanyCoreService {

    private final CompanyRepository companyRepository;

    public List<Company> findAllCompanies() {
        return companyRepository.findAll();
    }

    public Company findById(Long id) {
        return companyRepository.findById(id)
            .orElseThrow(() -> new IllegalArgumentException("매체사를 찾을 수 없습니다. ID: " + id));
    }
}
