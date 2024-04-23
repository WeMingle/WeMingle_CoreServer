package com.wemingle.core.domain.member.dto;

import com.wemingle.core.domain.category.sports.entity.sportstype.SportsType;
import com.wemingle.core.domain.post.entity.abillity.Ability;
import com.wemingle.core.domain.post.entity.area.AreaName;
import com.wemingle.core.domain.post.entity.gender.Gender;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;
import java.util.UUID;

@Setter
@Getter
@NoArgsConstructor
public class MemberInfoDto {

    @Setter
    @Getter
    @NoArgsConstructor
    public static class EachAbilityAboutMember{
        private Ability ability;
        private SportsType sportsType;

        @Builder
        public EachAbilityAboutMember(Ability ability, SportsType sportsType) {
            this.ability = ability;
            this.sportsType = sportsType;
        }
    }
    private String nickname;
    private boolean isMajorActivityAreaPublic;
    private AreaName majorActivityArea;
    private int numberOfMatches;
    private Gender gender;
    private boolean isAbilityPublic;
    private List<EachAbilityAboutMember> abilityList;
    private String oneLineIntroduction;
    private UUID profilePicId;
    private Integer birthYear;
    private boolean isBirthYearPublic;

    @Builder
    public MemberInfoDto(String nickname, boolean isMajorActivityAreaPublic, AreaName majorActivityArea, int numberOfMatches, Gender gender, boolean isAbilityPublic, List<EachAbilityAboutMember> abilityList, String oneLineIntroduction, UUID profilePicId, Integer birthYear, boolean isBirthYearPublic) {
        this.nickname = nickname;
        this.isMajorActivityAreaPublic = isMajorActivityAreaPublic;
        this.majorActivityArea = majorActivityArea;
        this.numberOfMatches = numberOfMatches;
        this.gender = gender;
        this.isAbilityPublic = isAbilityPublic;
        this.abilityList = abilityList;
        this.oneLineIntroduction = oneLineIntroduction;
        this.profilePicId = profilePicId;
        this.birthYear = birthYear;
        this.isBirthYearPublic = isBirthYearPublic;
    }
}

