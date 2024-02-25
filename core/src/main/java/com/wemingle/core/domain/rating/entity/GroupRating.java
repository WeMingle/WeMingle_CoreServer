package com.wemingle.core.domain.rating.entity;

import com.wemingle.core.domain.group.entity.Group;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;

import java.math.BigDecimal;

@Entity
public class GroupRating {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "PK")
    private Long pk;

    @NotNull
    @Column(name = "TOTAL_RATING")
    private BigDecimal totalRating;

    @NotNull
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "GROUP")
    private Group group;
}
