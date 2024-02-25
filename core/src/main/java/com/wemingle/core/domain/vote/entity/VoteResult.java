package com.wemingle.core.domain.vote.entity;

import com.wemingle.core.domain.common.entity.BaseEntity;
import com.wemingle.core.domain.group.entity.GroupMember;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;

@Entity
public class VoteResult extends BaseEntity {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "PK")
    private Long pk;

    @NotNull
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "GROUP_MEMBER")
    private GroupMember groupMember;

    @NotNull
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "VOTE_ITEM")
    private VoteItem voteItem;
}
