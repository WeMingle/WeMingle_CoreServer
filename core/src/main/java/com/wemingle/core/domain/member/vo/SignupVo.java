package com.wemingle.core.domain.member.vo;

import com.wemingle.core.domain.member.entity.Member;
import com.wemingle.core.domain.member.entity.phonetype.PhoneType;
import com.wemingle.core.domain.member.entity.role.Role;
import com.wemingle.core.domain.member.entity.signupplatform.SignupPlatform;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.web.multipart.MultipartFile;

import java.util.UUID;

public class SignupVo {
    @Getter
    @Setter
    @NoArgsConstructor
    public static class SaveMemberVo {
        String memberId;
        String password;
        String refreshToken;
        String firebaseToken;
        boolean notifyAllow;
        PhoneType phoneType;
        SignupPlatform signupPlatform;

        @Builder
        public SaveMemberVo(String memberId, String password, String refreshToken, String firebaseToken, boolean notifyAllow, PhoneType phoneType, SignupPlatform signupPlatform) {
            this.memberId = memberId;
            this.password = password;
            this.refreshToken = refreshToken;
            this.firebaseToken = firebaseToken;
            this.notifyAllow = notifyAllow;
            this.phoneType = phoneType;
            this.signupPlatform = signupPlatform;
        }

        public void patchPassword(String password){
            this.password = password;
        }

        public Member of(SaveMemberVo saveMemberVo){
            String randomUUID = UUID.randomUUID().toString();

            return Member.builder()
                    .memberId(saveMemberVo.getMemberId())
                    .password(saveMemberVo.getPassword())
                    .nickname(randomUUID)
                    .profileImgId(randomUUID)
                    .phoneType(saveMemberVo.getPhoneType())
                    .signupPlatform(saveMemberVo.getSignupPlatform())
                    .refreshToken(saveMemberVo.getRefreshToken())
                    .firebaseToken(saveMemberVo.getFirebaseToken())
                    .role(Role.UNVERIFIED_USER)
                    .complaintsCount(0)
                    .notifyAllow(saveMemberVo.isNotifyAllow())
                    .policyTerms(null)
                    .build();
        }
    }

    @Getter
    @Setter
    @NoArgsConstructor
    public static class PatchMemberProfileVo {
        String memberId;
        MultipartFile memberProfileImg;
        String nickname;

        @Builder
        public PatchMemberProfileVo(String memberId, MultipartFile memberProfileImg, String nickname) {
            this.memberId = memberId;
            this.memberProfileImg = memberProfileImg;
            this.nickname = nickname;
        }
    }

}
