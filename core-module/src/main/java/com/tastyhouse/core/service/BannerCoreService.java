package com.tastyhouse.core.service;

import com.tastyhouse.core.common.PageResult;
import com.tastyhouse.core.entity.banner.Banner;
import com.tastyhouse.core.entity.banner.dto.BannerListItemDto;
import com.tastyhouse.core.repository.banner.BannerJpaRepository;
import com.tastyhouse.core.repository.banner.BannerRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class BannerCoreService {

    private final BannerRepository bannerRepository;
    private final BannerJpaRepository bannerJpaRepository;

    public PageResult<BannerListItemDto> findAllWithPagination(int page, int size) {
        PageRequest pageRequest = PageRequest.of(page, size);
        Page<BannerListItemDto> bannerPage = bannerRepository.findAllWithFilter(pageRequest);
        return PageResult.from(bannerPage);
    }

    public Banner findById(Long id) {
        return bannerJpaRepository.findById(id).orElse(null);
    }
}
