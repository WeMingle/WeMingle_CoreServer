package com.wemingle.core.domain.team.dto;

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
}
