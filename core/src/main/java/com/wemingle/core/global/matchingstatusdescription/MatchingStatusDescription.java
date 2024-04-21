package com.wemingle.core.global.matchingstatusdescription;

import lombok.Getter;

@Getter
public enum MatchingStatusDescription {
    CANCEL_MATCHING("취소됨"),
    COMPLETE_MATCHING("완료됨"),
    REMAIN_DAYS_PREFIX("D-"),
    RENEW_MATCHING_POST("매칭글 다시 올리기"),
    CANCEL_NOT_PERMITTED_DURATION("취소할 수 있는 기간이 아닙니다"),
    CANCEL_BY_CHAT("채팅으로 신청 취소하기"),
    BEFORE_WRITE_REVIEW("매칭 일지 작성"),
    AFTER_WRITE_REVIEW("매칭 일지 작성 완료"),
    NO_PERMISSION("권한이 없습니다");

    private final String description;

    MatchingStatusDescription(String description) {
        this.description = description;
    }
}
