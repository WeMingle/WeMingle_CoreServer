package com.wemingle.core.domain.team.dto;

import com.wemingle.core.domain.team.entity.recruitmenttype.RecruitmentType;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.util.LinkedHashMap;

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
    public static class ResponseTeamInfoByName{
        private boolean hasNextTeam;
        private LinkedHashMap<Long, TeamInfoInSearch> teamsInfo;

        @Builder
        public ResponseTeamInfoByName(boolean hasNextTeam, LinkedHashMap<Long, TeamInfoInSearch> teamsInfo) {
            this.hasNextTeam = hasNextTeam;
            this.teamsInfo = teamsInfo;
        }
    }

    @Getter
    @NoArgsConstructor
    public static class TeamInfoInSearch {
        private String teamName;
        private String content;
        private String teamImgUrl;
        private RecruitmentType recruitmentType;

        @Builder
        public TeamInfoInSearch(String teamName, String content, String teamImgUrl, RecruitmentType recruitmentType) {
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
    @NoArgsConstructor
    public static class TeamInfo {
        private LocalDate createDate;
        private int teamMemberCnt;
        private String teamImgUrl;
        private String teamName;
        private double teamRating;
        private int reviewCnt;
        private String content;

        @Builder
        public TeamInfo(LocalDate createDate, int teamMemberCnt, String teamImgUrl, String teamName, double teamRating, int reviewCnt, String content) {
            this.createDate = createDate;
            this.teamMemberCnt = teamMemberCnt;
            this.teamImgUrl = teamImgUrl;
            this.teamName = teamName;
            this.teamRating = teamRating;
            this.reviewCnt = reviewCnt;
            this.content = content;
        }
    }
}
