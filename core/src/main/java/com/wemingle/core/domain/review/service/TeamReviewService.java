package com.wemingle.core.domain.review.service;

import com.wemingle.core.domain.member.entity.Member;
import com.wemingle.core.domain.post.entity.MatchingPost;
import com.wemingle.core.domain.review.dto.ReviewDto;

import java.util.List;

public interface TeamReviewService {
    List<MatchingPost> getMatchingPostsWithReviews(Member member);

    List<ReviewDto> getMyReviews(String memberId, Long nextIdx);

    List<ReviewDto> getGroupReviews(Long groupId, Long nextIdx);
}
