package com.wemingle.core.domain.report.entity;

import com.wemingle.core.domain.common.entity.BaseEntity;
import com.wemingle.core.domain.group.entity.TeamMember;
import com.wemingle.core.domain.post.entity.TeamPost;
import com.wemingle.core.domain.report.entity.reportinfo.ReportInfo;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;

@Entity
public class ReportTeamPost extends BaseEntity {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "PK")
    private Long pk;

    @NotNull
    @Embedded
    private ReportInfo reportInfo;

    @NotNull
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "TEAM_POST")
    private TeamPost teamPost;

    @NotNull
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "TEAM_MEMBER")
    private TeamMember teamMember;
}
