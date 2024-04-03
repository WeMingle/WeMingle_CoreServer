package com.wemingle.core.domain.member.dto;

import com.wemingle.core.domain.post.entity.abillity.Ability;
import com.wemingle.core.domain.post.entity.gender.Gender;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class MemberInfoDto {
    private String nickname;
    private boolean isMajorActivityAreaPublic;
    private String majorActivityArea;
    private int numberOfMatches;
    private Gender gender;
    private boolean isAbilityPublic;
    private Ability ability;
    private String OneLineIntroduction;

    @Builder

    public MemberInfoDto(String nickname, boolean isMajorActivityAreaPublic, String majorActivityArea, int numberOfMatches, Gender gender, boolean isAbilityPublic, Ability ability, String oneLineIntroduction) {
        this.nickname = nickname;
        this.isMajorActivityAreaPublic = isMajorActivityAreaPublic;
        this.majorActivityArea = majorActivityArea;
        this.numberOfMatches = numberOfMatches;
        this.gender = gender;
        this.isAbilityPublic = isAbilityPublic;
        this.ability = ability;
        this.OneLineIntroduction = oneLineIntroduction;
    }
}
