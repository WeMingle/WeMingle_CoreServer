package com.wemingle.core.domain.member.vo;

import com.wemingle.core.domain.member.entity.signupplatform.SignupPlatform;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.web.multipart.MultipartFile;

public class SignupVo {
    @Getter
    @Setter
    @NoArgsConstructor
    public static class SaveMemberVo {
        String memberId;
        String password;
        SignupPlatform signupPlatform;

        @Builder
        public SaveMemberVo(String memberId, String password, SignupPlatform signupPlatform) {
            this.memberId = memberId;
            this.password = password;
            this.signupPlatform = signupPlatform;
        }
    }

    @Getter
    @Setter
    @NoArgsConstructor
    public static class PatchMemberProfileVo {
        MultipartFile memberProfileImg;
        String nickname;

        @Builder
        public PatchMemberProfileVo(MultipartFile memberProfileImg, String nickname) {
            this.memberProfileImg = memberProfileImg;
            this.nickname = nickname;
        }
    }

}
