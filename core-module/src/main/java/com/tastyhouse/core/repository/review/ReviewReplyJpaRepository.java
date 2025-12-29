package com.tastyhouse.core.repository.review;

import com.tastyhouse.core.entity.review.ReviewReply;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ReviewReplyJpaRepository extends JpaRepository<ReviewReply, Long> {

    List<ReviewReply> findByCommentIdAndIsHiddenFalseOrderByCreatedAtAsc(Long commentId);

    List<ReviewReply> findByCommentIdInAndIsHiddenFalseOrderByCreatedAtAsc(List<Long> commentIds);
}
