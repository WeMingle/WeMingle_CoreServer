package com.wemingle.core.domain.member.entity;

import com.wemingle.core.domain.category.sports.entity.SportsCategory;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;

@Entity
public class MemberPreferenceSports {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "PK", nullable = false)
    private Long pk;

    @NotNull
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "MEMBER")
    private Member member;

    @NotNull
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "SPORTS")
    private SportsCategory sports;

}