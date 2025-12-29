package com.tastyhouse.webapi.review.response;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.List;

@Getter
@AllArgsConstructor
@Schema(description = "댓글 목록 응답")
public class CommentListResponse {

    @Schema(description = "댓글 목록")
    private List<CommentResponse> comments;

    @Schema(description = "총 댓글 수 (답글 포함)", example = "15")
    private int totalCount;
}
