package com.tastyhouse.webapi.report;

import com.tastyhouse.core.entity.report.BugReport;
import com.tastyhouse.core.entity.report.BugReportImage;
import com.tastyhouse.core.repository.report.BugReportImageJpaRepository;
import com.tastyhouse.core.repository.report.BugReportJpaRepository;
import com.tastyhouse.webapi.report.request.BugReportCreateRequest;
import com.tastyhouse.webapi.report.response.BugReportResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class BugReportService {

    private final BugReportJpaRepository bugReportJpaRepository;
    private final BugReportImageJpaRepository bugReportImageJpaRepository;

    @Transactional
    public BugReportResponse createBugReport(Long memberId, BugReportCreateRequest request) {
        BugReport bugReport = BugReport.builder()
            .memberId(memberId)
            .device(request.device())
            .title(request.title())
            .content(request.content())
            .build();

        BugReport savedReport = bugReportJpaRepository.save(bugReport);

        List<String> imageUrls = request.imageUrls();
        if (imageUrls != null && !imageUrls.isEmpty()) {
            for (int i = 0; i < imageUrls.size(); i++) {
                BugReportImage image = BugReportImage.builder()
                    .bugReportId(savedReport.getId())
                    .imageUrl(imageUrls.get(i))
                    .sort(i)
                    .build();
                bugReportImageJpaRepository.save(image);
            }
        }

        return BugReportResponse.builder()
            .id(savedReport.getId())
            .device(savedReport.getDevice())
            .title(savedReport.getTitle())
            .content(savedReport.getContent())
            .imageUrls(imageUrls)
            .createdAt(savedReport.getCreatedAt())
            .build();
    }
}
