package com.wemingle.core.domain.matching.entity;

import com.wemingle.core.domain.group.entity.Group;
import com.wemingle.core.domain.matching.entity.pendingstatus.PendingStatus;
import com.wemingle.core.domain.member.entity.Member;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;

@Entity
public class GroupJoinPending {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "pk")
    private Long pk;

    @NotNull
    @Enumerated(EnumType.STRING)
    private PendingStatus pendingStatus;

    @NotNull
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "GROUP")
    private Group group;

    @NotNull
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "MEMBER")
    private Member member;
}
