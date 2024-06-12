package com.wemingle.core.domain.report.entity;

import jakarta.persistence.Column;
import jakarta.persistence.DiscriminatorValue;
import jakarta.persistence.Entity;
import lombok.AccessLevel;
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
}
