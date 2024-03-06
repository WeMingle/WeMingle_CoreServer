package com.wemingle.core.domain.matching.dto.matchingprocess;

import lombok.Getter;

@Getter
public enum MatchingProcessType {
    APPROVAL("Approval"), NON_APPROVAL("Non-approval");
    private final String matchingProcess;

    MatchingProcessType(String matchingProcess) {
        this.matchingProcess = matchingProcess;
    }
}
