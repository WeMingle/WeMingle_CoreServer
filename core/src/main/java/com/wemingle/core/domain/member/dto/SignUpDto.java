package com.wemingle.core.domain.member.dto;

import com.wemingle.core.domain.member.entity.Member;
import com.wemingle.core.domain.member.entity.role.Role;
import com.wemingle.core.domain.member.signupplatform.Platform;
import com.wemingle.core.domain.member.vo.SignupVo;
import jakarta.validation.constraints.*;
import lombok.Value;
import com.wemingle.core.domain.member.entity.signupplatform.SignupPlatform;
import com.wemingle.core.global.annotation.Essential;
import jakarta.validation.constraints.Size;
import lombok.*;

import java.util.UUID;

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

        public SignupVo.SaveMemberVo of() {
            return SignupVo.SaveMemberVo.builder()
                    .memberId(memberId)
                    .password(password)
                    .signupPlatform(signupPlatform)
                    .build();
        }
    }
}