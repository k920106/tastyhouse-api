package com.tastyhouse.core.repository.report;

import com.tastyhouse.core.entity.report.BugReport;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface BugReportJpaRepository extends JpaRepository<BugReport, Long> {

    List<BugReport> findByMemberIdOrderByCreatedAtDesc(Long memberId);
}
