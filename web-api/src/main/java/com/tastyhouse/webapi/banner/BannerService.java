package com.tastyhouse.webapi.banner;

import com.tastyhouse.core.common.PageResult;
import com.tastyhouse.core.entity.banner.dto.BannerListItemDto;
import com.tastyhouse.core.service.BannerCoreService;
import com.tastyhouse.webapi.banner.response.BannerListItem;
import com.tastyhouse.webapi.common.PageRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class BannerService {

    private final BannerCoreService bannerCoreService;

    public PageResult<BannerListItem> findBannerList(PageRequest pageRequest) {
        return bannerCoreService.findAllWithPagination(
            pageRequest.getPage(), pageRequest.getSize()
        ).map(this::convertToBannerListItem);
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
