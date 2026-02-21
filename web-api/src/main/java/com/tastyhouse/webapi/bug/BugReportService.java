package com.tastyhouse.webapi.bug;

import com.tastyhouse.core.entity.report.BugReport;
import com.tastyhouse.core.entity.report.BugReportImage;
import com.tastyhouse.core.repository.report.BugReportImageJpaRepository;
import com.tastyhouse.core.repository.report.BugReportJpaRepository;
import com.tastyhouse.webapi.bug.request.BugReportCreateRequest;
import com.tastyhouse.webapi.bug.response.BugReportResponse;
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

        List<Long> uploadedFileIds = request.uploadedFileIds();
        if (uploadedFileIds != null && !uploadedFileIds.isEmpty()) {
            for (int i = 0; i < uploadedFileIds.size(); i++) {
                BugReportImage image = BugReportImage.builder()
                    .bugReportId(savedReport.getId())
                    .uploadedFileId(uploadedFileIds.get(i))
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
            .uploadedFileIds(uploadedFileIds)
            .createdAt(savedReport.getCreatedAt())
            .build();
    }
}
