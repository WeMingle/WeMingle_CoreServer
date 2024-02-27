package com.wemingle.core.domain.member.dto;

import com.wemingle.core.domain.member.vo.SignupVo;
import lombok.Getter;
import org.springframework.web.multipart.MultipartFile;

@Getter
public class SetMemberProfileDto {
    MultipartFile profilePic;
    String nickname;

    public SignupVo.PatchMemberProfileVo of() {
        return SignupVo.PatchMemberProfileVo.builder()
                .nickname(nickname)
                .memberProfileImg(profilePic)
                .build();
    }
}
