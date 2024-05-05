package com.wemingle.core.domain.team.repository;

import com.wemingle.core.domain.team.entity.Team;
import com.wemingle.core.domain.team.entity.TeamQuestionnaire;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface TeamQuestionnaireRepository extends JpaRepository<TeamQuestionnaire,Long> {
    @Query("select tq.content from TeamQuestionnaire tq where tq.team = :team")
    List<String> findContentByTeam(@Param("team")Team team);
}
