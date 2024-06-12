package com.wemingle.core.domain.report.entity;

import com.wemingle.core.domain.member.entity.Member;
import com.wemingle.core.domain.report.entity.reportitem.ReportItem;
import com.wemingle.core.domain.report.entity.reporttype.ReportPath;
import jakarta.persistence.Column;
import jakarta.persistence.DiscriminatorValue;
import jakarta.persistence.Entity;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.NoArgsConstructor;

@Entity
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@DiscriminatorValue("TEAM_POST")
public class TeamPostReportEntity extends ReportEntity {

    @Column
    private Long reportTeamPostId;

    @Column
    private String reportTeamPostContent;

    @Builder
    public TeamPostReportEntity(ReportItem reportItem, ReportPath reportPath, Member reporter, Member reportedMember, Long reportTeamPostId, String reportTeamPostContent) {
        super(reportItem, reportPath, reporter, reportedMember);
        this.reportTeamPostId = reportTeamPostId;
        this.reportTeamPostContent = reportTeamPostContent;
    }
}
