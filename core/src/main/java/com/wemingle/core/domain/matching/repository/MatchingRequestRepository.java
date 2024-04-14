package com.wemingle.core.domain.matching.repository;

import com.wemingle.core.domain.matching.entity.MatchingRequest;
import com.wemingle.core.domain.post.entity.MatchingPost;
import com.wemingle.core.domain.post.entity.matchingstatus.MatchingStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface MatchingRequestRepository extends JpaRepository<MatchingRequest, Long>,DSLMatchingRequestRepository {
    @Query("select count(*) from MatchingRequest m " +
            "where m.member.memberId = :memberId and m.matchingPost.matchingStatus = :matchingPostStatus and m.matchingRequestStatus = :matchingRequestStatus ")
    Integer findRequestedMatchingCnt(@Param("memberId") String memberId, @Param("matchingPostStatus") MatchingStatus matchingPostStatus, @Param("matchingRequestStatus") MatchingStatus matchingRequestStatus);

    @Query("select mr from MatchingRequest mr join fetch mr.team join fetch mr.member " +
            "where mr.matchingPost = :matchingPost and mr.matchingPost.recruiterType = 'INDIVIDUAL' and mr.matchingRequestStatus = 'PENDING' " +
            "order by mr.pk desc")
    List<MatchingRequest> findIndividualRequests(@Param("matchingPost") MatchingPost matchingPost);

    @Query("select mr from MatchingRequest mr join fetch mr.team " +
            "where mr.matchingPost = :matchingPost and mr.matchingPost.recruiterType = 'TEAM' and mr.team.teamOwner = mr.member and mr.matchingRequestStatus = 'PENDING' " +
            "order by mr.pk desc")
    List<MatchingRequest> findTeamRequestsIsOwner(@Param("matchingPost") MatchingPost matchingPost);
}
