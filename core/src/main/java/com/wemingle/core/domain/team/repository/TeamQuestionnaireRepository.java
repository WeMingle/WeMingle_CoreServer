package com.wemingle.core.domain.team.repository;

import com.wemingle.core.domain.team.entity.Team;
import com.wemingle.core.domain.team.entity.TeamQuestionnaire;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface TeamQuestionnaireRepository extends JpaRepository<TeamQuestionnaire,Long> {
    List<TeamQuestionnaire> findByTeamOrderByPkAsc(Team team);
}
