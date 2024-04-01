package com.wemingle.core.domain.matching.dto.partnertype;

import lombok.Getter;

@Getter
public enum MatchingPartnerType {
    INDIVIDUAL("개인"),GROUP("단체");
    private final String matchingPartner;

    MatchingPartnerType(String matchingPartner) {
        this.matchingPartner = matchingPartner;
    }
}
