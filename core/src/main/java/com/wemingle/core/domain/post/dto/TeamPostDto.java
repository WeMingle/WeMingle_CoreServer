package com.wemingle.core.domain.post.dto;

import com.wemingle.core.domain.post.entity.posttype.PostType;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;

public class TeamPostDto {
    @Getter
    @NoArgsConstructor
    public static class ResponseTeamPostsInfoWithMember {
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
    public static class ResponseTeamPostsInfoWithTeam {
        private boolean isTeamOwner;
        HashMap<Long, TeamPostInfo> teamPostsInfo;

        @Builder
        public ResponseTeamPostsInfoWithTeam(boolean isTeamOwner, HashMap<Long, TeamPostInfo> teamPostsInfo) {
            this.isTeamOwner = isTeamOwner;
            this.teamPostsInfo = teamPostsInfo;
        }
    }

    @Getter
    @NoArgsConstructor
    public static class TeamPostInfo {
        private String teamName;
        private String title;
        private String content;
        private String nickname;
        private LocalDateTime createdTime;
        private List<String> teamPostImgUrls;
        private PostType postType;
        private int likeCnt;
        private int replyCnt;
        private boolean isBookmarked;
        private VoteInfo voteInfo;

        @Builder
        public TeamPostInfo(String teamName, String title, String content, String nickname, LocalDateTime createdTime, List<String> teamPostImgUrls, PostType postType, int likeCnt, int replyCnt, boolean isBookmarked, VoteInfo voteInfo) {
            this.teamName = teamName;
            this.title = title;
            this.content = content;
            this.nickname = nickname;
            this.createdTime = createdTime;
            this.teamPostImgUrls = teamPostImgUrls;
            this.postType = postType;
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