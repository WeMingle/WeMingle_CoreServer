package com.wemingle.core.domain.group.entity;

import com.wemingle.core.domain.category.sports.entity.SportsCategory;
import com.wemingle.core.domain.common.entity.BaseEntity;
import com.wemingle.core.domain.group.entity.recruitmenttype.RecruitmentType;
import com.wemingle.core.domain.member.entity.Member;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;

@Entity
public class Group extends BaseEntity {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "PK")
    private Long pk;

    @NotNull
    @Column(name = "GROUP_NAME", columnDefinition = "VARBINARY(255) NOT NULL")
    private String groupName;

    @NotNull
    @Enumerated(EnumType.STRING)
    private RecruitmentType recruitmentType;

    @NotNull
    @Column(name = "CAPACITY_LIMIT")
    private int capacityLimit;

    @NotNull
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "GROUP_OWNER")
    private Member groupOwner;

    @NotNull
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "CATEGORY")
    private SportsCategory sportsCategory;
}
