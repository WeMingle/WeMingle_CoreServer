package com.wemingle.core.domain.matching.repository;

import com.wemingle.core.domain.matching.entity.MatchingRequest;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface MatchingRequestRepository extends JpaRepository<MatchingRequest, Long> {
//    @Query("select count(*) from MatchingRequest m where m.teamMember.member.memberId = :memberId ")
//    Integer findRequestedMatchingCnt(@Param("memberId") String memberId);

    @Query("select count(*) from MatchingRequest m where m.matchingPost.writer.member.memberId = :memberId ")
    Integer findReceivedMatchingCnt(@Param("memberId") String memberId);
}
