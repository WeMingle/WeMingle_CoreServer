package com.wemingle.core.global.exceptionmessage;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum ExceptionMessage {
    MEMBER_NOT_FOUNT("사용자를 찾을 수 없습니다"),
    POST_NOT_FOUND("글을 찾을 수 없습니다."),
    TEAM_NOT_FOUND("팀을 찾을 수 없습니다."),
    TEMPORARY_TEAM_NOT_FOUND("임시 팀을 찾을 수 없습니다."),
    TEAM_MEMBER_NOT_FOUND("팀에 속한 사용자 찾을 수 없습니다."),
    MATCHING_DATE_NOT_FOUND("매칭 날짜를 찾을 수 없습니다."),
    UNIV_DOMAIN_NOT_FOUND("미지원 대학 도메인입니다."),
    UNAVAILABLE_NICKNAME("이미 사용중인 닉네임입니다"),
    UNAVAILABLE_EMAIL("이미 가입된 이메일입니다"),
    IS_EXPIRED_REFRESH_AND_ACCESS_TOKEN("Refresh와 Access Token이 만료되었습니다. 사용자 인증을 다시해주세요"),
    UNREGISTERED_MEMBER("가입되지 않은 사용자입니다."),
    NOT_VERIFIED_UNIV_EMAIL("이메일 인증을 완료하지 않은 사용자입니다."),
    DATE_MONTH_CANT_COEXIST("일별 검색과 월별 검색 옵션이 모두 존재할 수 없습니다"),
    DATE_OR_MONTH_MUST_EXIST("일별 검색 또는 월별 검색 옵션이 존재해야 합니다."),
    INVALID_MATCHING_POST_STATUS("유효하지 않은 매칭 글 상태입니다"),
    INVALID_MATCHING_REQUEST_STATUS("유효하지 않은 매칭 신청 상태입니다"),
    INVALID_REQUEST_TYPE("유효하지 않은 요청 타입입니다."),
    INVALID_TEAM__QUESTIONNAIRE_PK("유효하지 않은 팀 설문조사 pk입니다."),
    MATCHING_POST_NOT_FOUND("매칭 글을 찾을 수 없습니다."),
    VERIFICATION_CODE_ERROR("유효하지 않은 인증코드입니다."),
    LIKE_CNT_LESS_THAN_ZERO("좋아요 수는 0보다 작을 수 없습니다."),
    REPLY_CNT_LESS_THAN_ZERO("댓글 수는 0보다 작을 수 없습니다."),
    COMMENT_NOT_FOUND("댓글을 찾을 수 없습니다."),
    REPLY_NOT_FOUND("대댓글을 찾을 수 없습니다."),
    VOTE_NOT_FOUND("투표를 찾을 수 없습니다."),
    TEAM_REQUEST_NOT_FOUND("팀 신청 내역을 찾을 수 없습니다."),
    BOOKMARKED_NOT_FOUND("북마크 내역을 찾을 수 없습니다.");

    private final String exceptionMessage;
}
