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

import java.util.UUID;

@Entity
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@DiscriminatorValue("CHAT")
public class ChatReportEntity extends ReportEntity {
    @Column(name = "reportRoomId")
    private UUID reportRoomId;

    @Builder
    public ChatReportEntity(ReportItem reportItem, ReportPath reportPath, Member reporter, Member reportedMember, UUID reportRoomId) {
        super(reportItem, reportPath, reporter, reportedMember);
        this.reportRoomId = reportRoomId;
    }
}
