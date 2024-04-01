package com.wemingle.core.domain.matching.dto.gengertype;

import lombok.Getter;

@Getter
public enum GenderType {
    MALE("남성"),FEMALE("여성");

    private final String gender;

    GenderType(String gender) {
        this.gender = gender;
    }
}
