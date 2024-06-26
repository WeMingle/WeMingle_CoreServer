package com.wemingle.core.domain.review.repository;

import com.wemingle.core.domain.member.entity.Member;
import com.wemingle.core.domain.post.entity.MatchingPost;
import com.wemingle.core.domain.review.entity.TeamReview;
import com.wemingle.core.domain.team.entity.Team;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface TeamReviewRepository extends JpaRepository<TeamReview, Long>,DSLReviewRepository {
    @Query("select tr.matchingPost from TeamReview tr where tr.reviewer = :member")
    List<MatchingPost> findMatchingPostWithMemberId(@Param("member") Member member);
    @Query("select count(*) from TeamReview tr where tr.reviewee = :team")
    Integer findTeamReviewCntWithReviewee(@Param("team") Team team);
}
