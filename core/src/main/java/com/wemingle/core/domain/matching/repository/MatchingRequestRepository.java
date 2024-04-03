package com.wemingle.core.domain.matching.repository;

import com.wemingle.core.domain.matching.entity.MatchingRequest;
import com.wemingle.core.domain.post.entity.matchingstatus.MatchingStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface MatchingRequestRepository extends JpaRepository<MatchingRequest, Long>,DSLMatchingRequestRepository {
    @Query("select count(*) from MatchingRequest m " +
            "where m.member.memberId = :memberId and m.matchingPost.matchingStatus = :matchingPostStatus and m.matchingRequestStatus = :matchingRequestStatus ")
    Integer findRequestedMatchingCnt(@Param("memberId") String memberId, @Param("matchingPostStatus") MatchingStatus matchingPostStatus, @Param("matchingRequestStatus") MatchingStatus matchingRequestStatus);
}
