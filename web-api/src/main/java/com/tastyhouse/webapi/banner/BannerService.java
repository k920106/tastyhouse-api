package com.tastyhouse.webapi.banner;

import com.tastyhouse.core.entity.banner.dto.BannerListItemDto;
import com.tastyhouse.core.service.BannerCoreService;
import com.tastyhouse.webapi.banner.response.BannerListItem;
import com.tastyhouse.webapi.common.PageRequest;
import com.tastyhouse.webapi.common.PageResult;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class BannerService {

    private final BannerCoreService bannerCoreService;

    public PageResult<BannerListItem> findBannerList(PageRequest pageRequest) {
        BannerCoreService.BannerPageResult coreResult = bannerCoreService.findAllWithPagination(
            pageRequest.getPage(), pageRequest.getSize()
        );

        List<BannerListItem> bannerListItems = coreResult.getContent().stream()
            .map(this::convertToBannerListItem)
            .toList();

        return new PageResult<>(
            bannerListItems,
            coreResult.getTotalElements(),
            coreResult.getTotalPages(),
            coreResult.getCurrentPage(),
            coreResult.getPageSize()
        );
    }

    private BannerListItem convertToBannerListItem(BannerListItemDto dto) {
        return BannerListItem.builder()
            .id(dto.getId())
            .title(dto.getTitle())
            .imageUrl(dto.getImageUrl())
            .linkUrl(dto.getLinkUrl())
            .build();
    }
}
