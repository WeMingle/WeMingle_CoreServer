package com.wemingle.core.domain.member.dto;

import com.wemingle.core.domain.post.entity.abillity.Ability;
import com.wemingle.core.domain.post.entity.gender.Gender;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class MemberInfoDto {
    private String majorActivityArea;
    private int numberOfMatches;
    private Gender gender;
    private Ability ability;
    private String OneLineIntroduction;

    @Builder
    public MemberInfoDto(String majorActivityArea, int numberOfMatches, Gender gender, Ability ability, String oneLineIntroduction) {
        this.majorActivityArea = majorActivityArea;
        this.numberOfMatches = numberOfMatches;
        this.gender = gender;
        this.ability = ability;
        OneLineIntroduction = oneLineIntroduction;
    }
}
