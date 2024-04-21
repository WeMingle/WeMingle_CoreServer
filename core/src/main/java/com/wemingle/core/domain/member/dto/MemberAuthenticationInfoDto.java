package com.wemingle.core.domain.member.dto;

import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class MemberAuthenticationInfoDto {
    private String memberId;
    private String univEmail;

    @Builder
    public MemberAuthenticationInfoDto(String memberId, String univEmail) {
        this.memberId = memberId;
        this.univEmail = univEmail;
    }
}
