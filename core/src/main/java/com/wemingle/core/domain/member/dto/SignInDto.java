package com.wemingle.core.domain.member.dto;

import com.wemingle.core.domain.authentication.dto.TokenDto;
import com.wemingle.core.domain.member.entity.signupplatform.SignupPlatform;
import com.wemingle.core.global.annotation.Essential;
import jakarta.validation.constraints.Size;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

public class SignInDto {
    @Getter
    @NoArgsConstructor
    public static class ResponseSignInDto{
        @Essential
        TokenDto.ResponseTokenDto token;
        Boolean email;
        Boolean onboard;

        @Builder
        public ResponseSignInDto(TokenDto.ResponseTokenDto token, Boolean email, Boolean onboard) {
            this.token = token;
            this.email = email;
            this.onboard = onboard;
        }
    }
}
