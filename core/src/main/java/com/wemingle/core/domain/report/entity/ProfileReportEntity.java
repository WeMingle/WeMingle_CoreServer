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
@DiscriminatorValue("PROFILE")
public class ProfileReportEntity extends ReportEntity {
    @Column(name = "nickname")
    private String nickname;
    @Column(name = "profileImgId")
    private UUID profileImgId;

    @Builder
    public ProfileReportEntity(ReportItem reportItem, ReportPath reportPath, Member reporter, Member reportedMember, String nickname, UUID profileImgId) {
        super(reportItem, reportPath, reporter, reportedMember);
        this.nickname = nickname;
        this.profileImgId = profileImgId;
    }
}
