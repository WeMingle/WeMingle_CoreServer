package com.wemingle.core.domain.matching.entity;

import com.wemingle.core.domain.group.entity.Team;
import com.wemingle.core.domain.matching.entity.pendingstatus.PendingStatus;
import com.wemingle.core.domain.member.entity.Member;
import com.wemingle.core.domain.post.entity.MatchingPost;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;

@Entity
public class MatchingPending {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "pk")
    private Long pk;

    @NotNull
    @Column(name = "CONTENT")
    private String content;

    @NotNull
    @Enumerated(EnumType.STRING)
    private PendingStatus pendingStatus;

    @NotNull
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "MATCHING_POST")
    private MatchingPost matchingPost;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "TEAM")
    private Team team;

    @NotNull
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "MEMBER")
    private Member member;
}
