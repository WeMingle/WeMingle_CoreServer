package com.wemingle.core.domain.team.repository;

import com.wemingle.core.domain.member.entity.Member;
import com.wemingle.core.domain.team.entity.TeamQuestionnaire;
import com.wemingle.core.domain.team.entity.TeamQuestionnaireAnswer;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface TeamQuestionnaireAnswerRepository extends JpaRepository<TeamQuestionnaireAnswer,Long> {
    List<TeamQuestionnaireAnswer> findByTeamQuestionnaireInAndRequester(List<TeamQuestionnaire> teamQuestionnaires, Member requester);
}
