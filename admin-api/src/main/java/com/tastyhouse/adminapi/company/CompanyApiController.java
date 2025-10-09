package com.tastyhouse.adminapi.company;

import com.tastyhouse.adminapi.company.response.CompanyListItem;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/companies")
public class CompanyApiController {

    private final CompanyService companyService;

    @GetMapping
    public ResponseEntity<List<CompanyListItem>> getCompanyList() {
        List<CompanyListItem> companies = companyService.findCompanyList();
        return ResponseEntity.ok(companies);
    }
}
