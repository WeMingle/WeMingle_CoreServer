package com.wemingle.core.domain.review.repository;

import com.wemingle.core.domain.review.entity.TeamReview;

import java.util.List;

public interface DSLReviewRepository {
    List<TeamReview> findMyReviews(String memberId, Long nextIdx);

    List<TeamReview> findGroupReviews(Long groupId, Long nextIdx);
}
