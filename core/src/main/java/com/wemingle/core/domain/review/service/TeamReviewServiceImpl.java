package com.wemingle.core.domain.review.service;

import com.wemingle.core.domain.member.entity.Member;
import com.wemingle.core.domain.post.entity.MatchingPost;
import com.wemingle.core.domain.review.dto.ReviewDto;
import com.wemingle.core.domain.review.entity.TeamReview;
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

    @Override
    public List<ReviewDto> getMyReviews(String memberId, Long nextIdx) {
        return teamReviewRepository.findMyReviews(memberId, nextIdx)
                .stream()
                .map(teamReview -> ReviewDto.builder()
                        .pk(teamReview.getPk())
                        .rating(teamReview.getRating())
                        .reviewer(teamReview.getReviewer().getTeam().getTeamName())
                        .content(teamReview.getContent())
                        .createTime(teamReview.getCreatedTime())
                        .build())
                .toList();
    }

    @Override
    public List<ReviewDto> getGroupReviews(Long groupId, Long nextIdx) {
        return teamReviewRepository.findGroupReviews(groupId, nextIdx)
                .stream()
                .map(teamReview -> ReviewDto.builder()
                        .pk(teamReview.getPk())
                        .rating(teamReview.getRating())
                        .reviewer(teamReview.getReviewer().getTeam().getTeamName())
                        .content(teamReview.getContent())
                        .createTime(teamReview.getCreatedTime())
                        .build())
                .toList();
    }
}
