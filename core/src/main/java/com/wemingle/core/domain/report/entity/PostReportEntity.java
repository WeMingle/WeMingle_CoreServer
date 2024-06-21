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
@DiscriminatorValue("POST")
public class PostReportEntity extends ReportEntity {

    @Column
    private Long reportPostId;
    @Column
    private String reportPostContent;

    @Builder
    public PostReportEntity(ReportItem reportItem, ReportPath reportPath, Member reporter, Member reportedMember, Long reportPostId, String reportPostContent) {
        super(reportItem, reportPath, reporter, reportedMember);
        this.reportPostId = reportPostId;
        this.reportPostContent = reportPostContent;
    }
}
