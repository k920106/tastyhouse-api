package com.tastyhouse.core.repository.review;

import com.tastyhouse.core.entity.review.dto.BestReviewListItemDto;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface ReviewRepository {

    Page<BestReviewListItemDto> findBestReviews(Pageable pageable);
}