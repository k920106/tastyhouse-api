package com.tastyhouse.adminapi.company;

import com.tastyhouse.adminapi.company.response.CompanyListItem;
import com.tastyhouse.core.service.CompanyCoreService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class CompanyService {

    private final CompanyCoreService companyCoreService;

    public List<CompanyListItem> findCompanyList() {
        return companyCoreService.findAllCompanies()
                .stream()
                .map(company -> new CompanyListItem(company.getId(), company.getName()))
                .toList();
    }
}
