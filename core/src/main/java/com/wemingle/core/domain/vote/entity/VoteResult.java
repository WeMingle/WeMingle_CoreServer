package com.wemingle.core.domain.vote.entity;

import com.wemingle.core.domain.common.entity.BaseEntity;
import com.wemingle.core.domain.group.entity.TeamMember;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;

@Entity
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
    @JoinColumn(name = "VOTE_ITEM")
    private VoteItem voteItem;
}
