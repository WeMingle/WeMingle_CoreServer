package com.wemingle.core.domain.authentication.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

public class TokenDto {
    @Getter
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static class RequestTokenDto{
        private String refreshToken;
        private String accessToken;
    }

    @Getter
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static class ResponseTokenDto{
        private String refreshToken;
        private String accessToken;
    }
}
