package com.wemingle.core.domain.team.repository;

import com.wemingle.core.domain.team.entity.Team;
import com.wemingle.core.domain.team.entity.teamtype.TeamType;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface TeamRepository extends JpaRepository<Team, Long>, DSLTeamRepository{
    List<Team> findByTeamOwner_MemberId(String memberId);
    boolean existsByPkLessThanAndTeamNameContainsAndTeamType(Long teamPk, String teamName, TeamType teamType);
}
