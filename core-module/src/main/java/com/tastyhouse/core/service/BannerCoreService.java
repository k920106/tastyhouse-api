package com.tastyhouse.core.service;

import com.tastyhouse.core.entity.banner.Banner;
import com.tastyhouse.core.entity.banner.dto.BannerListItemDto;
import com.tastyhouse.core.repository.banner.BannerJpaRepository;
import com.tastyhouse.core.repository.banner.BannerRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class BannerCoreService {

    private final BannerRepository bannerRepository;
    private final BannerJpaRepository bannerJpaRepository;

    public BannerPageResult findAllWithPagination(int page, int size) {
        PageRequest pageRequest = PageRequest.of(page, size);
        Page<BannerListItemDto> bannerPage = bannerRepository.findAllWithFilter(
            pageRequest
        );

        return new BannerPageResult(
            bannerPage.getContent(),
            bannerPage.getTotalElements(),
            bannerPage.getTotalPages(),
            bannerPage.getNumber(),
            bannerPage.getSize()
        );
    }

    public Banner findById(Long id) {
        return bannerJpaRepository.findById(id).orElse(null);
    }

    public static class BannerPageResult {
        private final List<BannerListItemDto> content;
        private final long totalElements;
        private final int totalPages;
        private final int currentPage;
        private final int pageSize;

        public BannerPageResult(List<BannerListItemDto> content, long totalElements, int totalPages, int currentPage, int pageSize) {
            this.content = content;
            this.totalElements = totalElements;
            this.totalPages = totalPages;
            this.currentPage = currentPage;
            this.pageSize = pageSize;
        }

        public List<BannerListItemDto> getContent() { return content; }
        public long getTotalElements() { return totalElements; }
        public int getTotalPages() { return totalPages; }
        public int getCurrentPage() { return currentPage; }
        public int getPageSize() { return pageSize; }
    }
}
