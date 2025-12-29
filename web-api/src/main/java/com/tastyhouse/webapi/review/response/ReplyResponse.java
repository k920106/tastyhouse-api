package com.tastyhouse.webapi.review.response;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

import java.time.LocalDateTime;

@Getter
@Builder
@AllArgsConstructor
@Schema(description = "답글 응답")
public class ReplyResponse {

    @Schema(description = "답글 ID", example = "1")
    private Long id;

    @Schema(description = "댓글 ID", example = "1")
    private Long commentId;

    @Schema(description = "작성자 ID", example = "1")
    private Long memberId;

    @Schema(description = "작성자 닉네임", example = "맛집헌터")
    private String memberNickname;

    @Schema(description = "작성자 프로필 이미지 URL")
    private String memberProfileImageUrl;

    @Schema(description = "답글 대상 멤버 ID", example = "2")
    private Long replyToMemberId;

    @Schema(description = "답글 대상 멤버 닉네임", example = "맛집러버")
    private String replyToMemberNickname;

    @Schema(description = "답글 내용", example = "저도 그렇게 생각해요!")
    private String content;

    @Schema(description = "작성일시")
    private LocalDateTime createdAt;
}
