package com.wemingle.core.domain.post.dto;

import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;

public class TeamPostDto {
    @Getter
    @NoArgsConstructor
    public static class ResponseTeamPostsInfoWithMember{
        private String teamName;
        private String title;
        private String content;
        private String nickname;
        private LocalDateTime createdTime;
        private List<String> teamPostImgUrls;
        private int likeCnt;
        private int replyCnt;
        private boolean isBookmarked;
        private VoteInfo voteInfo;

        @Builder
        public ResponseTeamPostsInfoWithMember(String teamName, String title, String content, String nickname, LocalDateTime createdTime, List<String> teamPostImgUrls, int likeCnt, int replyCnt, boolean isBookmarked, VoteInfo voteInfo) {
            this.teamName = teamName;
            this.title = title;
            this.content = content;
            this.nickname = nickname;
            this.createdTime = createdTime;
            this.teamPostImgUrls = teamPostImgUrls;
            this.likeCnt = likeCnt;
            this.replyCnt = replyCnt;
            this.isBookmarked = isBookmarked;
            this.voteInfo = voteInfo;
        }
    }

    @Getter
    @NoArgsConstructor
    public static class VoteInfo{
        private Long votePk;
        private List<VoteOptionInfo> voteOptionInfos;

        @Builder
        public VoteInfo(Long votePk, List<VoteOptionInfo> voteOptionInfos) {
            this.votePk = votePk;
            this.voteOptionInfos = voteOptionInfos;
        }
    }

    @Getter
    @NoArgsConstructor
    public static class VoteOptionInfo{
        private String optionName;
        private int resultCnt;

        @Builder
        public VoteOptionInfo(String optionName, int resultCnt) {
            this.optionName = optionName;
            this.resultCnt = resultCnt;
        }
    }
}