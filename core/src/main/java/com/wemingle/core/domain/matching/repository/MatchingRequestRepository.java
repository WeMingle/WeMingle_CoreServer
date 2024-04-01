package com.wemingle.core.domain.matching.repository;

import com.wemingle.core.domain.matching.entity.MatchingRequest;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface MatchingRequestRepository extends JpaRepository<MatchingRequest, Long>,DSLMatchingRequestRepository {
    @Query("select count(*) from MatchingRequest m where m.member.memberId = :memberId ")
    Integer findRequestedMatchingCnt(@Param("memberId") String memberId);

//    @Query("select count(*) from MatchingRequest m join fetch m.matchingPost.writer.team.teamMembers where m.matchingPost.writer.team.teamMembers in :memberId ")
//    Integer findReceivedMatchingCnt(@Param("memberId") String memberId);
}
