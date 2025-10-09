package com.tastyhouse.webapi.reviews.request;

import org.springframework.web.multipart.MultipartFile;

public record ReviewCreateRequest(
    String placeName,
    String menuName,
    String content,
    MultipartFile[] photos,
    String[] tags
) {
}
