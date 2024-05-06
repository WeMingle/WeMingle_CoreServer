package com.wemingle.core.domain.matching.repository;

import com.wemingle.core.domain.matching.entity.TeamRequest;
import com.wemingle.core.domain.member.entity.Member;
import com.wemingle.core.domain.team.entity.Team;
import org.springframework.data.jpa.repository.JpaRepository;

public interface TeamRequestRepository extends JpaRepository<TeamRequest, Long> {
    boolean existsByTeamAndRequester(Team team, Member requester);
}