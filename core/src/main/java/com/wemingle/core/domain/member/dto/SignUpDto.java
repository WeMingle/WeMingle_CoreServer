package com.wemingle.core.domain.member.dto;

import com.wemingle.core.domain.member.entity.signupplatform.SignupPlatform;
import com.wemingle.core.global.annotation.Essential;
import jakarta.validation.constraints.Size;
import jakarta.validation.constraints.*;
import lombok.Value;

/**
 * DTO for {@link com.wemingle.core.domain.member.entity.Member}
 */
public class SignUpDto {
    @Value
    public static class RequestSignUpDto{
        @Essential
        String memberId;

        @Essential
        @Size(min = 8, max = 20)
        String password;

        @Essential
        SignupPlatform signupPlatform;
    }
}