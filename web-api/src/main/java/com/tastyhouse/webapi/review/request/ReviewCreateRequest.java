package com.tastyhouse.webapi.review.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import org.springframework.web.multipart.MultipartFile;

public record ReviewCreateRequest(
    @NotBlank(message = "매장명은 필수입니다")
    @Size(max = 200, message = "매장명은 200자 이내로 입력해주세요")
    String placeName,

    @NotBlank(message = "메뉴명은 필수입니다")
    @Size(max = 200, message = "메뉴명은 200자 이내로 입력해주세요")
    String menuName,

    @NotBlank(message = "리뷰 내용은 필수입니다")
    String content,

    MultipartFile[] photos,

    String[] tags
) {
}
