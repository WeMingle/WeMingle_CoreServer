package com.wemingle.core.domain.report.entity;

import com.wemingle.core.domain.common.entity.BaseEntity;
import com.wemingle.core.domain.member.entity.Member;
import com.wemingle.core.domain.post.entity.MatchingPost;
import com.wemingle.core.domain.report.entity.reportinfo.ReportInfo;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;

@Entity
public class ReportMatchingPost extends BaseEntity {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "PK")
    private Long pk;

    @NotNull
    @Embedded
    private ReportInfo reportInfo;

    @NotNull
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "MATCHING_POST")
    private MatchingPost matchingPost;

    @NotNull
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "MEMBER")
    private Member member;
}
