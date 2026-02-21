package com.tastyhouse.core.repository.report;

import com.querydsl.jpa.impl.JPAQueryFactory;
import com.tastyhouse.core.entity.report.BugReport;
import com.tastyhouse.core.entity.report.BugReportImage;
import com.tastyhouse.core.entity.report.QBugReport;
import com.tastyhouse.core.entity.report.QBugReportImage;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
@RequiredArgsConstructor
public class ReportRepositoryImpl implements ReportRepository {

    private final JPAQueryFactory queryFactory;

    @Override
    public List<BugReport> findBugReportsByMemberIdOrderByCreatedAtDesc(Long memberId) {
        QBugReport bugReport = QBugReport.bugReport;

        return queryFactory
            .selectFrom(bugReport)
            .where(bugReport.memberId.eq(memberId))
            .orderBy(bugReport.createdAt.desc())
            .fetch();
    }

    @Override
    public List<BugReportImage> findBugReportImagesByBugReportId(Long bugReportId) {
        QBugReportImage bugReportImage = QBugReportImage.bugReportImage;

        return queryFactory
            .selectFrom(bugReportImage)
            .where(bugReportImage.bugReportId.eq(bugReportId))
            .orderBy(bugReportImage.sort.asc())
            .fetch();
    }
}
