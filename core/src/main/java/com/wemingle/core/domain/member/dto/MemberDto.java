package com.wemingle.core.domain.member.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

public class MemberDto {
    @Getter
    @Setter
    @NoArgsConstructor
    public static class ResponseMemberInfoInSearch {
        private String nickname;
        private String profileImg;
        @JsonProperty(value = "isMe")
        private boolean isMe;
        //todo 채팅구현됐을 시 1대1대화방 어떤식으로 데이터 줄건 지 확인 후 추가 구현

        @Builder
        public ResponseMemberInfoInSearch(String nickname, String profileImg, boolean isMe) {
            this.nickname = nickname;
            this.profileImg = profileImg;
            this.isMe = isMe;
        }
    }
}
