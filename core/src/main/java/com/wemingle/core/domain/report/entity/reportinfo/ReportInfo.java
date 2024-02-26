package com.wemingle.core.domain.report.entity.reportinfo;

import com.wemingle.core.domain.report.entity.reporttype.ReportType;
import jakarta.persistence.Embeddable;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;

@Embeddable
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class ReportInfo {
    private String reportContent;
    private ReportType reportType;
}
