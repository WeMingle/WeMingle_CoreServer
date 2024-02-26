package com.wemingle.core.domain.review.entity;

import com.wemingle.core.domain.common.entity.BaseEntity;
import com.wemingle.core.domain.group.entity.Team;
import com.wemingle.core.domain.member.entity.Member;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;

@Entity
public class TeamReview extends BaseEntity {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "PK")
    private Long pk;

    @NotNull
    @Column(name = "CONTENT", length = 3000)
    private String content;

    @NotNull
    @Column(name = "RATING")
    private int rating;

    @NotNull
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "REVIEWEE")
    private Team reviewee;

    @NotNull
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "REVIEWER")
    private Member reviewer;
}
