package com.wemingle.core.domain.member.entity.signupplatform;

public enum SignupPlatform {
    KAKAO("카카오"),
    NAVER("네이버"),
    GOOGLE("구글"),
    APPLE("애플"),
    NONE("자체 회원가입");

    private final String platformType;

    public String getPlatformType() {
        return platformType;
    }

    SignupPlatform(String platformType) {
        this.platformType = platformType;
    }
}
