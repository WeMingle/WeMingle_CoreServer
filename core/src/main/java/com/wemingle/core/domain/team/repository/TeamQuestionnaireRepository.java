package com.wemingle.core.domain.team.repository;

import com.wemingle.core.domain.team.entity.Team;
import com.wemingle.core.domain.team.entity.TeamQuestionnaire;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface TeamQuestionnaireRepository extends JpaRepository<TeamQuestionnaire,Long> {
    @Query("select tq from TeamQuestionnaire tq where tq.team = :team and tq.isDeleted = false " +
            "order by tq.pk asc")
    List<TeamQuestionnaire> findActiveByTeam(Team team);
}
