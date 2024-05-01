package com.wemingle.core.domain.member.dto;

import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

public class TeamMemberDto {
    @Getter
    @Setter
    @NoArgsConstructor
    public static class ResponseTeamMembers{
        private String nickname;
        private String imgUrl;

        @Builder
        public ResponseTeamMembers(String nickname, String imgUrl) {
            this.nickname = nickname;
            this.imgUrl = imgUrl;
        }
    }
}
