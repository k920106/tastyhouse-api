package com.tastyhouse.core.repository.notice;

import com.tastyhouse.core.entity.notice.Notice;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface NoticeJpaRepository extends JpaRepository<Notice, Long> {
}
