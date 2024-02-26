package com.wemingle.core.domain.vote.entity;

import com.wemingle.core.domain.common.entity.BaseEntity;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;

@Entity
public class TeamPostVote extends BaseEntity {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "PK")
    private Long pk;

    @NotNull
    @Column(name = "TITLE", length = 1000)
    private String title;

    @NotNull
    @Column(name = "END_TIME")
    private String endTime;

    @NotNull
    @Column(name = "IS_MULTI_VOTING")
    private boolean isMultiVoting;

    @NotNull
    @Column(name = "IS_ANONYMOUS_VOTING")
    private boolean isAnonymousVoting;

    @NotNull
    @Column(name = "IS_ADDITIONAL_ITEM")
    private boolean isAdditionalItem;
}
