package com.wemingle.core.domain.report.entity;

import com.wemingle.core.domain.common.entity.BaseEntity;
import com.wemingle.core.domain.member.entity.Member;
import com.wemingle.core.domain.report.entity.reportitem.ReportItem;
import com.wemingle.core.domain.report.entity.reporttype.ReportPath;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;

@Entity
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Inheritance(strategy = InheritanceType.JOINED)
@DiscriminatorColumn(name = "reportType")
public abstract class ReportEntity extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotNull
    @Column(name = "reportItem")
    private ReportItem reportItem;

    @NotNull
    @Column(name = "reportPath")
    private ReportPath reportPath;

    @NotNull
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "reporter")
    private Member reporter;

    @NotNull
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "reportedMember")
    private Member reportedMember;

    public ReportEntity(ReportItem reportItem, ReportPath reportPath, Member reporter, Member reportedMember) {
        this.reportItem = reportItem;
        this.reportPath = reportPath;
        this.reporter = reporter;
        this.reportedMember = reportedMember;
    }
}
