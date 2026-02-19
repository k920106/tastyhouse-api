package com.tastyhouse.core.repository.report;

import com.tastyhouse.core.entity.report.BugReport;
import com.tastyhouse.core.entity.report.BugReportImage;
import com.tastyhouse.core.entity.report.PlaceReport;

import java.util.List;

public interface ReportRepository {

    List<BugReport> findBugReportsByMemberIdOrderByCreatedAtDesc(Long memberId);

    List<BugReportImage> findBugReportImagesByBugReportId(Long bugReportId);

    List<PlaceReport> findAllPlaceReportsOrderByCreatedAtDesc();
}
