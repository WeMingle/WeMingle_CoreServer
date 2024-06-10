package com.wemingle.core.domain.team.entity;

import com.wemingle.core.domain.common.entity.BaseEntity;
import com.wemingle.core.domain.member.entity.Member;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Entity
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class TeamQuestionnaireAnswer extends BaseEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "PK")
    private Long pk;

    @NotNull
    @Column
    private String answer;

    @NotNull
    @ManyToOne
    @JoinColumn(name = "TEAM_QUESTIONNAIRE")
    private TeamQuestionnaire teamQuestionnaire;

    @NotNull
    @ManyToOne
    @JoinColumn(name = "REQUESTER")
    private Member requester;

    @Builder
    public TeamQuestionnaireAnswer(String answer, TeamQuestionnaire teamQuestionnaire, Member requester) {
        this.answer = answer;
        this.teamQuestionnaire = teamQuestionnaire;
        this.requester = requester;
    }
}
