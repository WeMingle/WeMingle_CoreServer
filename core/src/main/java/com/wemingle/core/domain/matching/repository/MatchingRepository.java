package com.wemingle.core.domain.matching.repository;

import com.wemingle.core.domain.matching.entity.Matching;
import com.wemingle.core.domain.member.entity.Member;
import com.wemingle.core.domain.post.entity.MatchingPost;
import com.wemingle.core.domain.team.entity.Team;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface MatchingRepository extends JpaRepository<Matching, Long>, DSLMatchingRepository{
//    @Query("select count(*) from Matching m " +
//            "where m.member.memberId = :memberId and m.matchingPost.matchingStatus = :matchingStatus and DATE(Now()) > DATE(MAX(m.matchingPost.matchingDates)) ")
//    Integer findCompleteMatchingCnt(@Param("memberId") String memberId, @Param("matchingStatus")MatchingStatus matchingStatus);

//    @Query("select count(*) from Matching m " +
//            "where m.member.memberId = :memberId and m.matchingPost.matchingStatus = :matchingStatus and DATE(Now()) < DATE(MIN(m.matchingPost.matchingDates)) ")
//    Integer findScheduledMatchingCnt(@Param("memberId") String memberId, @Param("matchingStatus")MatchingStatus matchingStatus);

    List<Matching> findByMatchingPost(MatchingPost matchingPost);

    @Query("select m from Matching m where m.matchingPost in :matchingPostList and m.member.memberId = :memberId")
    List<Matching> findByMatchingResultByMemberId(@Param("memberId") String memberId, @Param("matchingPostList") List<MatchingPost> matchingPostList);
    @Query("select m.member from Matching m where m.team = :team and m.matchingPost = :matchingPost")
    List<Member> findMatchingPostTeamParticipants(@Param("team") Team team, @Param("matchingPost") MatchingPost matchingPost);
}
