package com.tastyhouse.core.repository.report;

import com.tastyhouse.core.entity.report.PlaceReport;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface PlaceReportJpaRepository extends JpaRepository<PlaceReport, Long> {
    List<PlaceReport> findAllByOrderByCreatedAtDesc();
}
