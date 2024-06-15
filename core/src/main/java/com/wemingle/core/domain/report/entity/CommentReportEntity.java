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
@DiscriminatorValue("COMMENT")
public class CommentReportEntity extends ReportEntity{

    @Column(name = "reportCommentId")
    private Long reportCommentId;

    @Column(name = "reportCommentContent")
    private String reportCommentContent;

    @Builder
    public CommentReportEntity(ReportItem reportItem, ReportPath reportPath, Member reporter, Member reportedMember, Long reportCommentId, String reportCommentContent) {
        super(reportItem, reportPath, reporter, reportedMember);
        this.reportCommentId = reportCommentId;
        this.reportCommentContent = reportCommentContent;
    }
}
