package com.wemingle.core.domain.vote.entity;

import com.wemingle.core.domain.common.entity.BaseEntity;
import com.wemingle.core.domain.team.entity.TeamMember;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Getter
public class VoteResult extends BaseEntity {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "PK")
    private Long pk;

    @NotNull
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "TEAM_MEMBER")
    private TeamMember teamMember;

    @NotNull
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "VOTE_OPTION")
    private VoteOption voteOption;

    @Builder
    public VoteResult(TeamMember teamMember, VoteOption voteOption) {
        this.teamMember = teamMember;
        this.voteOption = voteOption;
    }
}
