package com.wemingle.core.domain.review.service;

import com.wemingle.core.domain.member.entity.Member;
import com.wemingle.core.domain.post.entity.MatchingPost;
import com.wemingle.core.domain.review.repository.TeamReviewRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class TeamReviewServiceImpl implements TeamReviewService{
    private final TeamReviewRepository teamReviewRepository;
    @Override
    public List<MatchingPost> getMatchingPostsWithReviews(Member member) {
        return teamReviewRepository.findMatchingPostWithMemberId(member);
    }
}
