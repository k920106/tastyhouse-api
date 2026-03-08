package com.tastyhouse.webapi.banner;

import com.tastyhouse.core.common.PageResult;
import com.tastyhouse.core.entity.banner.dto.BannerListItemDto;
import com.tastyhouse.core.service.BannerCoreService;
import com.tastyhouse.webapi.banner.response.BannerListItem;
import com.tastyhouse.webapi.common.PageRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Service
@RequiredArgsConstructor
public class BannerService {

    private final BannerCoreService bannerCoreService;

    @Transactional(readOnly = true)
    public PageResult<BannerListItem> searchBannerList(PageRequest pageRequest) {
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
