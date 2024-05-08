package com.wemingle.core.domain.matching.vo;

import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class IsExceedCapacityLimitVo {
    private Long matchingPostPk;
    private int capacityCnt;

    @Builder
    public IsExceedCapacityLimitVo(Long matchingPostPk, int capacityCnt) {
        this.matchingPostPk = matchingPostPk;
        this.capacityCnt = capacityCnt;
    }
}
