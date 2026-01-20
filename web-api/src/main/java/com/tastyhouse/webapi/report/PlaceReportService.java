package com.tastyhouse.webapi.report;

import com.tastyhouse.core.entity.report.PlaceReport;
import com.tastyhouse.core.repository.report.PlaceReportJpaRepository;
import com.tastyhouse.webapi.report.request.PlaceReportCreateRequest;
import com.tastyhouse.webapi.report.response.PlaceReportResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class PlaceReportService {

    private final PlaceReportJpaRepository placeReportJpaRepository;

    @Transactional
    public PlaceReportResponse createPlaceReport(PlaceReportCreateRequest request) {
        PlaceReport placeReport = PlaceReport.of(
            request.businessName(),
            request.address(),
            request.addressDetail()
        );

        PlaceReport savedReport = placeReportJpaRepository.save(placeReport);

        return PlaceReportResponse.builder()
            .id(savedReport.getId())
            .businessName(savedReport.getBusinessName())
            .address(savedReport.getAddress())
            .addressDetail(savedReport.getAddressDetail())
            .createdAt(savedReport.getCreatedAt())
            .build();
    }
}
