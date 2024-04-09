package com.wemingle.core.domain.matching.repository;

import com.wemingle.core.domain.matching.entity.Matching;
import com.wemingle.core.domain.post.entity.matchingstatus.MatchingStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface MatchingRepository extends JpaRepository<Matching, Long>, DSLMatchingRequestRepository{
    @Query("select count(*) from Matching m " +
            "where m.member.memberId = :memberId and m.matchingPost.matchingStatus = :matchingStatus and DATE(Now()) > DATE(m.matchingPost.matchingDate) ")
    Integer findCompleteMatchingCnt(@Param("memberId") String memberId, @Param("matchingStatus")MatchingStatus matchingStatus);

    @Query("select count(*) from Matching m " +
            "where m.member.memberId = :memberId and m.matchingPost.matchingStatus = :matchingStatus and DATE(Now()) < DATE(m.matchingPost.matchingDate) ")
    Integer findScheduledMatchingCnt(@Param("memberId") String memberId, @Param("matchingStatus")MatchingStatus matchingStatus);
}
