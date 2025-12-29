package com.tastyhouse.webapi.review.response;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

import java.time.LocalDateTime;
import java.util.List;

@Getter
@Builder
@AllArgsConstructor
@Schema(description = "댓글 응답")
public class CommentResponse {

    @Schema(description = "댓글 ID", example = "1")
    private Long id;

    @Schema(description = "리뷰 ID", example = "1")
    private Long reviewId;

    @Schema(description = "작성자 ID", example = "1")
    private Long memberId;

    @Schema(description = "작성자 닉네임", example = "맛집러버")
    private String memberNickname;

    @Schema(description = "작성자 프로필 이미지 URL")
    private String memberProfileImageUrl;

    @Schema(description = "댓글 내용", example = "맛있어 보이네요!")
    private String content;

    @Schema(description = "작성일시")
    private LocalDateTime createdAt;

    @Schema(description = "답글 목록")
    private List<ReplyResponse> replies;
}
