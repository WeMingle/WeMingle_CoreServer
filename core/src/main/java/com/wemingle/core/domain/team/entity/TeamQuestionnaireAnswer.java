package com.wemingle.core.domain.team.entity;

import com.wemingle.core.domain.common.entity.BaseEntity;
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

    @Builder
    public TeamQuestionnaireAnswer(Long pk, String answer, TeamQuestionnaire teamQuestionnaire) {
        this.pk = pk;
        this.answer = answer;
        this.teamQuestionnaire = teamQuestionnaire;
    }
}
