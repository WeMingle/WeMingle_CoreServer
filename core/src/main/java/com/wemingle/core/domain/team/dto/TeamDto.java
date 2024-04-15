package com.wemingle.core.domain.team.dto;

import com.wemingle.core.domain.team.entity.recruitmenttype.RecruitmentType;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

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
    public static class ResponseRandomTeamInfo {
        private String teamName;
        private String content;
        private String teamImgUrl;
        private RecruitmentType recruitmentType;

        @Builder
        public ResponseRandomTeamInfo(String teamName, String content, String teamImgUrl, RecruitmentType recruitmentType) {
            this.teamName = teamName;
            this.content = content;
            this.teamImgUrl = teamImgUrl;
            this.recruitmentType = recruitmentType;
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
}
