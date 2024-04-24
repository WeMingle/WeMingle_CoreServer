package com.wemingle.core.domain.team.dto;

import com.wemingle.core.domain.category.sports.entity.sportstype.SportsType;
import com.wemingle.core.domain.post.entity.gender.Gender;
import com.wemingle.core.domain.team.entity.recruitmenttype.RecruitmentType;
import lombok.*;

import java.util.List;
import java.util.UUID;

@Getter
@Setter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class CreateTeamDto {
    private String teamName;
    private String content;
    private UUID teamImgId;
    private SportsType sportsType;
    private RecruitmentType recruitmentType;
    private Boolean onlySameUniv;
    private Boolean ageIsIrrelevant;
    private Integer startAge;
    private Integer endAge;
    private Boolean genderIsIrrelevant;
    private Gender gender;
    private Boolean personnelLimitIrrelevant;
    private Integer personnelLimit;
    private List<String> freeQuestionList;

    @Builder
    public CreateTeamDto(String teamName, String content, UUID teamImgId, SportsType sportsType, RecruitmentType recruitmentType, Boolean onlySameUniv, Boolean ageIsIrrelevant, Integer startAge, Integer endAge, Boolean genderIsIrrelevant, Gender gender, Boolean personnelLimitIrrelevant, Integer personnelLimit, List<String> freeQuestionList) {
        this.teamName = teamName;
        this.content = content;
        this.teamImgId = teamImgId;
        this.sportsType = sportsType;
        this.recruitmentType = recruitmentType;
        this.onlySameUniv = onlySameUniv;
        this.ageIsIrrelevant = ageIsIrrelevant;
        this.startAge = startAge;
        this.endAge = endAge;
        this.genderIsIrrelevant = genderIsIrrelevant;
        this.gender = gender;
        this.personnelLimitIrrelevant = personnelLimitIrrelevant;
        this.personnelLimit = personnelLimit;
        this.freeQuestionList = freeQuestionList;
    }
}
