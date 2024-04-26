package com.wemingle.core.domain.matching.repository;

import com.wemingle.core.domain.matching.entity.MatchingRequest;
import com.wemingle.core.domain.post.entity.MatchingPost;
import com.wemingle.core.domain.post.entity.matchingstatus.MatchingStatus;
import com.wemingle.core.domain.team.entity.Team;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Set;

public interface MatchingRequestRepository extends JpaRepository<MatchingRequest, Long>,DSLMatchingRequestRepository {
    @Query("select count(*) from MatchingRequest m " +
            "where m.member.memberId = :memberId and m.matchingPost.matchingStatus = :matchingPostStatus and m.matchingRequestStatus = :matchingRequestStatus ")
    Integer findRequestedMatchingCnt(@Param("memberId") String memberId, @Param("matchingPostStatus") MatchingStatus matchingPostStatus, @Param("matchingRequestStatus") MatchingStatus matchingRequestStatus);
    @Query("select mr from MatchingRequest mr join fetch mr.team join fetch mr.member " +
            "where mr.matchingPost = :matchingPost and mr.matchingPost.recruiterType = 'INDIVIDUAL' and mr.matchingRequestStatus = 'PENDING' " +
            "order by mr.pk desc")
    List<MatchingRequest> findIndividualRequests(@Param("matchingPost") MatchingPost matchingPost);
    @Query("select mr from MatchingRequest mr join fetch mr.team " +
            "where mr.matchingPost = :matchingPost and mr.matchingPost.recruiterType = 'TEAM' and mr.requestMemberType = 'REQUESTER' and mr.matchingRequestStatus = 'PENDING' " +
            "order by mr.pk desc")
    List<MatchingRequest> findTeamRequestsIsOwner(@Param("matchingPost") MatchingPost matchingPost);
    List<MatchingRequest> findByPkIn(List<Long> matchingRequestsPk);
    @Query("select mr from MatchingRequest  mr " +
            "where mr.matchingPost = :matchingPost and mr.team in :team and mr.matchingRequestStatus = 'PENDING'")
    List<MatchingRequest> findAllRequestsWithTeam(@Param("matchingPost") MatchingPost matchingPost,
                                                  @Param("team") Set<Team> team);

}
