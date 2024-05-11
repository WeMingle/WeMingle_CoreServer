package com.wemingle.core.domain.team.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.wemingle.core.domain.post.entity.gender.Gender;
import com.wemingle.core.domain.team.entity.recruitmenttype.RecruitmentType;
import com.wemingle.core.domain.team.entity.teamtype.TeamType;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDate;

public class TeamDto {
    @Getter
    @NoArgsConstructor
    public static class ResponseTeamInfoDto {
        String teamName;
        String teamImgUrl;

        @Builder
        public ResponseTeamInfoDto(String teamName, String teamImgUrl) {
            this.teamName = teamName;
            this.teamImgUrl = teamImgUrl;
        }
    }

    @Getter
    @NoArgsConstructor
    public static class ResponseRecommendationTeamInfo {
        private String teamName;
        private String content;
        private String teamImgUrl;
        private RecruitmentType recruitmentType;

        @Builder
        public ResponseRecommendationTeamInfo(String teamName, String content, String teamImgUrl, RecruitmentType recruitmentType) {
            this.teamName = teamName;
            this.content = content;
            this.teamImgUrl = teamImgUrl;
            this.recruitmentType = recruitmentType;
        }
    }

    @Getter
    @NoArgsConstructor
    public static class ResponseRecommendationTeamForMemberInfo {
        private String teamName;
        private String content;
        private String teamImgUrl;

        @Builder
        public ResponseRecommendationTeamForMemberInfo(String teamName, String content, String teamImgUrl) {
            this.teamName = teamName;
            this.content = content;
            this.teamImgUrl = teamImgUrl;
        }
    }

    @Getter
    @NoArgsConstructor
    public static class ResponseTeamInfoInSearch {
        private String teamName;
        private String content;
        private String teamImgUrl;
        private RecruitmentType recruitmentType;

        @Builder
        public ResponseTeamInfoInSearch(String teamName, String content, String teamImgUrl, RecruitmentType recruitmentType) {
            this.teamName = teamName;
            this.content = content;
            this.teamImgUrl = teamImgUrl;
            this.recruitmentType = recruitmentType;
        }
    }

    @Getter
    @NoArgsConstructor
    public static class ResponseTeamHomeConditions{
        private boolean isExistMyTeam;
        private boolean isUnivVerifiedMember;

        @Builder
        public ResponseTeamHomeConditions(boolean isExistMyTeam, boolean isUnivVerifiedMember) {
            this.isExistMyTeam = isExistMyTeam;
            this.isUnivVerifiedMember = isUnivVerifiedMember;
        }
    }

    @Getter
    @NoArgsConstructor
    public static class ResponseTeamByMemberUniv {
        String teamName;
        String teamImgUrl;

        @Builder
        public ResponseTeamByMemberUniv(String teamName, String teamImgUrl) {
            this.teamName = teamName;
            this.teamImgUrl = teamImgUrl;
        }
    }

    @Getter
    @Setter
    @NoArgsConstructor
    public static class TeamInfo {
        private LocalDate createDate;
        private int teamMemberCnt;
        private String teamImgUrl;
        private String teamBackgroundImgUrl;
        private String teamName;
        private double teamRating;
        private int reviewCnt;
        private String content;
        @JsonProperty(value = "isManager")
        private boolean isManager;

        @Builder
        public TeamInfo(LocalDate createDate, int teamMemberCnt, String teamImgUrl, String teamBackgroundImgUrl, String teamName, double teamRating, int reviewCnt, String content, boolean isManager) {
            this.createDate = createDate;
            this.teamMemberCnt = teamMemberCnt;
            this.teamImgUrl = teamImgUrl;
            this.teamBackgroundImgUrl = teamBackgroundImgUrl;
            this.teamName = teamName;
            this.teamRating = teamRating;
            this.reviewCnt = reviewCnt;
            this.content = content;
            this.isManager = isManager;
        }
    }

    @Getter
    @Setter
    @NoArgsConstructor
    public static class ResponseTeamParticipantCond {
        private boolean beforeWriteInfo;
        @JsonProperty("isTeamMember")
        private boolean isTeamMember;
        @JsonProperty("isTeamRequest")
        private boolean isTeamRequest;
        @JsonProperty("isExceedCapacity")
        private boolean isExceedCapacity;
        private Boolean univCondResult;
        private GenderCondResult genderCondResult;
        private BirthYearCondResult birthYearCondResult;

        @Builder
        public ResponseTeamParticipantCond(boolean beforeWriteInfo, boolean isTeamMember, boolean isTeamRequest, boolean isExceedCapacity, Boolean univCondResult, GenderCondResult genderCondResult, BirthYearCondResult birthYearCondResult) {
            this.beforeWriteInfo = beforeWriteInfo;
            this.isTeamMember = isTeamMember;
            this.isTeamRequest = isTeamRequest;
            this.isExceedCapacity = isExceedCapacity;
            this.univCondResult = univCondResult;
            this.genderCondResult = genderCondResult;
            this.birthYearCondResult = birthYearCondResult;
        }
    }

    @Getter
    @NoArgsConstructor
    public static class GenderCondResult {
        private boolean isSatisfiedGenderCond;
        private Gender gender;

        @Builder
        public GenderCondResult(boolean isSatisfiedGenderCond, Gender gender) {
            this.isSatisfiedGenderCond = isSatisfiedGenderCond;
            this.gender = gender;
        }
    }

    @Getter
    @NoArgsConstructor
    public static class BirthYearCondResult {
        private boolean isSatisfiedBirthYearCond;
        private int startAge;
        private int endAge;

        @Builder
        public BirthYearCondResult(boolean isSatisfiedBirthYearCond, int startAge, int endAge) {
            this.isSatisfiedBirthYearCond = isSatisfiedBirthYearCond;
            this.startAge = startAge;
            this.endAge = endAge;
        }
    }

    @Getter
    @NoArgsConstructor
    public static class ResponseWritableTeamInfoDto {
        private String teamName;
        private String teamImgUrl;
        private TeamType teamType;

        @Builder
        public ResponseWritableTeamInfoDto(String teamName, String teamImgUrl, TeamType teamType) {
            this.teamName = teamName;
            this.teamImgUrl = teamImgUrl;
            this.teamType = teamType;
        }
    }
}
