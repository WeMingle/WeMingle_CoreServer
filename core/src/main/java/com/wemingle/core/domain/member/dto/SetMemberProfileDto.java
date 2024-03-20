package com.wemingle.core.domain.member.dto;

import com.wemingle.core.domain.member.vo.SignupVo;
import com.wemingle.core.global.annotation.Essential;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class SetMemberProfileDto {
    @Essential
    @Size(min = 2, max = 10, message = "2~10글자 사이로 입력하세요")
    String nickname;

    public SignupVo.PatchMemberProfileVo of() {
        return SignupVo.PatchMemberProfileVo.builder()
                .nickname(nickname)
                .build();
    }
}
