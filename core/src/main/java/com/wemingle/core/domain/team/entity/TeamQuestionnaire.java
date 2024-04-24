package com.wemingle.core.domain.team.entity;

import com.wemingle.core.domain.common.entity.BaseEntity;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Entity
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class TeamQuestionnaire extends BaseEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "PK")
    private Long pk;

    @Column
    private String content;
    @ManyToOne
    @JoinColumn(name = "TEAM")
    Team team;

    @Builder
    public TeamQuestionnaire(String content, Team team) {
        this.content = content;
        this.team = team;
    }
}
