package com.wemingle.core.domain.team.dto;

import com.wemingle.core.domain.team.entity.recruitmenttype.RecruitmentType;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

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
}
