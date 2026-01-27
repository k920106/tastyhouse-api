package com.tastyhouse.webapi.policy;

import com.tastyhouse.core.common.CommonResponse;
import com.tastyhouse.core.entity.policy.PolicyType;
import com.tastyhouse.webapi.common.PageRequest;
import com.tastyhouse.webapi.common.PageResult;
import com.tastyhouse.webapi.policy.response.PolicyDetailResponse;
import com.tastyhouse.webapi.policy.response.PolicyListItemResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Tag(name = "Policy", description = "약관 및 정책 관리 API")
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/policies")
public class PolicyApiController {

    private final PolicyService policyService;

    @Operation(summary = "최신 이용약관 조회", description = "현재 유효한 최신 이용약관을 조회합니다.")
    @ApiResponses({@ApiResponse(responseCode = "200", description = "조회 성공", content = @Content(schema = @Schema(implementation = CommonResponse.class)))})
    @GetMapping("/v1/terms-of-service/latest")
    public ResponseEntity<CommonResponse<PolicyDetailResponse>> getLatestTermsOfService() {
        PolicyDetailResponse response = policyService.findCurrentByType(PolicyType.TERMS_OF_SERVICE);
        return ResponseEntity.ok(CommonResponse.success(response));
    }

    @Operation(summary = "최신 개인정보처리방침 조회", description = "현재 유효한 최신 개인정보처리방침을 조회합니다.")
    @ApiResponses({@ApiResponse(responseCode = "200", description = "조회 성공", content = @Content(schema = @Schema(implementation = CommonResponse.class)))})
    @GetMapping("/v1/privacy-policy/latest")
    public ResponseEntity<CommonResponse<PolicyDetailResponse>> getLatestPrivacyPolicy() {
        PolicyDetailResponse response = policyService.findCurrentByType(PolicyType.PRIVACY_POLICY);
        return ResponseEntity.ok(CommonResponse.success(response));
    }

    @Operation(summary = "특정 버전 이용약관 조회", description = "지정된 버전의 이용약관을 조회합니다.")
    @ApiResponses({@ApiResponse(responseCode = "200", description = "조회 성공", content = @Content(schema = @Schema(implementation = CommonResponse.class)))})
    @GetMapping("/v1/terms-of-service/version/{version}")
    public ResponseEntity<CommonResponse<PolicyDetailResponse>> getTermsOfServiceByVersion(@PathVariable String version) {
        PolicyDetailResponse response = policyService.findByTypeAndVersion(PolicyType.TERMS_OF_SERVICE, version);
        return ResponseEntity.ok(CommonResponse.success(response));
    }

    @Operation(summary = "특정 버전 개인정보처리방침 조회", description = "지정된 버전의 개인정보처리방침을 조회합니다.")
    @ApiResponses({@ApiResponse(responseCode = "200", description = "조회 성공", content = @Content(schema = @Schema(implementation = CommonResponse.class)))})
    @GetMapping("/v1/privacy-policy/version/{version}")
    public ResponseEntity<CommonResponse<PolicyDetailResponse>> getPrivacyPolicyByVersion(@PathVariable String version) {
        PolicyDetailResponse response = policyService.findByTypeAndVersion(PolicyType.PRIVACY_POLICY, version);
        return ResponseEntity.ok(CommonResponse.success(response));
    }

    @Operation(summary = "이용약관 목록 조회", description = "모든 버전의 이용약관 목록을 조회합니다. (관리자용)")
    @ApiResponses({@ApiResponse(responseCode = "200", description = "조회 성공", content = @Content(schema = @Schema(implementation = CommonResponse.class)))})
    @GetMapping("/v1/terms-of-service")
    public ResponseEntity<CommonResponse<List<PolicyListItemResponse>>> getTermsOfServiceList(
        @RequestParam(defaultValue = "0") int page,
        @RequestParam(defaultValue = "10") int size) {
        PageRequest pageRequest = new PageRequest(page, size);
        PageResult<PolicyListItemResponse> pageResult = policyService.findAllByType(PolicyType.TERMS_OF_SERVICE, pageRequest);
        CommonResponse<List<PolicyListItemResponse>> response = CommonResponse.success(
            pageResult.getContent(), page, size, pageResult.getTotalElements()
        );
        return ResponseEntity.ok(response);
    }

    @Operation(summary = "개인정보처리방침 목록 조회", description = "모든 버전의 개인정보처리방침 목록을 조회합니다. (관리자용)")
    @ApiResponses({@ApiResponse(responseCode = "200", description = "조회 성공", content = @Content(schema = @Schema(implementation = CommonResponse.class)))})
    @GetMapping("/v1/privacy-policy")
    public ResponseEntity<CommonResponse<List<PolicyListItemResponse>>> getPrivacyPolicyList(
        @RequestParam(defaultValue = "0") int page,
        @RequestParam(defaultValue = "10") int size) {
        PageRequest pageRequest = new PageRequest(page, size);
        PageResult<PolicyListItemResponse> pageResult = policyService.findAllByType(PolicyType.PRIVACY_POLICY, pageRequest);
        CommonResponse<List<PolicyListItemResponse>> response = CommonResponse.success(
            pageResult.getContent(), page, size, pageResult.getTotalElements()
        );
        return ResponseEntity.ok(response);
    }
}
