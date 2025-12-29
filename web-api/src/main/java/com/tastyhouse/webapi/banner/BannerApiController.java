package com.tastyhouse.webapi.banner;

import com.tastyhouse.core.common.PagedCommonResponse;
import com.tastyhouse.webapi.banner.response.BannerListItem;
import com.tastyhouse.webapi.common.PageRequest;
import com.tastyhouse.webapi.common.PageResult;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@Tag(name = "Banner", description = "배너 관리 API")
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/banners")
public class BannerApiController {

    private final BannerService bannerService;

    @Operation(summary = "배너 목록 조회", description = "페이징된 배너 목록을 조회합니다. 제목 검색과 활성화 상태 필터링이 가능합니다.")
    @ApiResponses({@ApiResponse(responseCode = "200", description = "조회 성공", content = @Content(schema = @Schema(implementation = PagedCommonResponse.class)))})
    @GetMapping("/v1")
    public ResponseEntity<PagedCommonResponse<BannerListItem>> getBannerList(@RequestParam(defaultValue = "0") int page, @RequestParam(defaultValue = "5") int size) {
        PageRequest pageRequest = new PageRequest(page, size);
        PageResult<BannerListItem> pageResult = bannerService.findBannerList(pageRequest);
        PagedCommonResponse<BannerListItem> response = PagedCommonResponse.success(pageResult.getContent(), page, size, pageResult.getTotalElements());
        return ResponseEntity.ok(response);
    }
}
