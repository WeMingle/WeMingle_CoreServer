package com.wemingle.core.domain.member.dto;

import com.wemingle.core.domain.member.entity.phonetype.PhoneType;
import com.wemingle.core.domain.member.entity.signupplatform.SignupPlatform;
import com.wemingle.core.domain.member.vo.SignupVo;
import com.wemingle.core.global.annotation.Essential;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * DTO for {@link com.wemingle.core.domain.member.entity.Member}
 */
public class SignUpDto {

    @Setter
    @Getter
    @NoArgsConstructor
    public static class RequestCheckAvailableIdDto{
        @Essential
        String memberId;
    }



    @Getter
    @NoArgsConstructor
    public static class RequestSignUpDto{
        @Essential
        String memberId;

        @Essential
        @Size(min = 8, max = 20)
        String password;

        @Essential
        SignupPlatform signupPlatform;

        @Essential
        PhoneType phoneType;

        @Essential
        String firebaseToken;

        @Essential
        boolean allowNotification;

        @Essential
        boolean agreeToLocationBasedServices;

        @Essential
        boolean agreeToReceiveMarketingInformation;

        public SignupVo.SaveMemberVo of() {
            return SignupVo.SaveMemberVo.builder()
                    .memberId(memberId)
                    .password(password)
                    .signupPlatform(signupPlatform)
                    .notifyAllow(allowNotification)
                    .firebaseToken(firebaseToken)
                    .phoneType(phoneType)
                    .agreeToReceiveMarketingInformation(agreeToReceiveMarketingInformation)
                    .agreeToLocationBasedServices(agreeToLocationBasedServices)
                    .build();
        }
    }
}