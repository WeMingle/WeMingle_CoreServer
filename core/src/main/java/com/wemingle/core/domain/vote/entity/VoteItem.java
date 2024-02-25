package com.wemingle.core.domain.vote.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;

@Entity
public class VoteItem {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "PK")
    private Long pk;

    @NotNull
    @Column(name = "ITEM_NAME")
    private String itemName;

    @NotNull
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "GROUP_POST_VOTE")
    private GroupPostVote groupPostVote;
}
