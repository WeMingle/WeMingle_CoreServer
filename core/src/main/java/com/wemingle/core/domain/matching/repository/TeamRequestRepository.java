package com.wemingle.core.domain.matching.repository;

import com.wemingle.core.domain.matching.entity.TeamRequest;
import com.wemingle.core.domain.member.entity.Member;
import com.wemingle.core.domain.team.entity.Team;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface TeamRequestRepository extends JpaRepository<TeamRequest, Long> {
    boolean existsByTeamAndRequester(Team team, Member requester);
    @Query("select tr from TeamRequest tr join fetch tr.requester where tr.team = :team order by tr.pk desc")
    List<TeamRequest> findByTeamFetchMember(@Param("team") Team team);
}
