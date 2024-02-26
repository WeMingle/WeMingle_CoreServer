package com.wemingle.core.domain.member.entity;

import com.wemingle.core.domain.common.entity.BaseEntity;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;

@Entity
public class PolicyTerms extends BaseEntity {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "pk")
    private Long pk;

    @NotNull
    @Column(name = "POLICY_1")
    private Boolean policy1;

    @NotNull
    @Column(name = "POLICY_2")
    private Boolean policy2;

    @NotNull
    @Column(name = "POLICY_3")
    private Boolean policy3;
}
