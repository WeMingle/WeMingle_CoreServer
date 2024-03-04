package com.wemingle.core.domain.memberunivemail.dto;

import com.wemingle.core.global.annotation.Essential;
import jakarta.validation.constraints.Email;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

public class MailDto {
    @Getter
    @NoArgsConstructor
    public static class RequestSendMailDto{
        @Essential @Email
        String univEmail;

        @Builder
        public RequestSendMailDto(String univEmail) {
            this.univEmail = univEmail;
        }
    }

    @Getter
    @NoArgsConstructor
    public static class ResponseRegisteredPlatformDto{
        String registeredPlatform;

        public ResponseRegisteredPlatformDto(String registeredPlatform) {
            this.registeredPlatform = registeredPlatform;
        }
    }

    @Getter
    @Setter
    @NoArgsConstructor
    public static class RequestVerifyCodeDto{
        @Essential @Email
        String univEmail;
        String verificationCode;

        @Builder
        public RequestVerifyCodeDto(String univEmail, String verificationCode) {
            this.univEmail = univEmail;
            this.verificationCode = verificationCode;
        }
    }
}
