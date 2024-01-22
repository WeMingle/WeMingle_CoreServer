package com.wemingle.core.global.exceptionmessage;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum ExceptionMessage {
    MEMBER_NOT_FOUNT("사용자를 찾을 수 없습니다"),
    UNIV_DOMAIN_NOT_FOUND("미지원 대학 도메인입니다.");
    private final String exceptionMessage;
}
