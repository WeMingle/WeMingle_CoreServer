package com.wemingle.core.global.exceptionmessage;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum ExceptionMessage {
    MEMBER_NOT_FOUNT("사용자를 찾을 수 없습니다"),
    POST_NOT_FOUND("글을 찾을 수 없습니다."),
    TEAM_NOT_FOUND("팀을 찾을 수 없습니다."),
    TEAM_MEMBER_NOT_FOUND("팀에 속한 사용자 찾을 수 없습니다."),
    UNIV_DOMAIN_NOT_FOUND("미지원 대학 도메인입니다."),
    UNAVAILABLE_NICKNAME("이미 사용중인 닉네임입니다"),
    UNAVAILABLE_EMAIL("이미 가입된 이메일입니다"),
    IS_EXPIRED_REFRESH_AND_ACCESS_TOKEN("Refresh와 Access Token이 만료되었습니다. 사용자 인증을 다시해주세요"),
    UNREGISTERED_MEMBER("가입되지 않은 사용자입니다."),
    INVALID_MATCHING_POST_STATUS("유효하지 않은 매칭 글 상태입니다");
    private final String exceptionMessage;
}
