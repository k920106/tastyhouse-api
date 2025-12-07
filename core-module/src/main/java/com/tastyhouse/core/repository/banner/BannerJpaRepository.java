package com.tastyhouse.core.repository.banner;

import com.tastyhouse.core.entity.banner.Banner;
import org.springframework.data.jpa.repository.JpaRepository;

public interface BannerJpaRepository extends JpaRepository<Banner, Long> {
}