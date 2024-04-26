package com.wemingle.core.domain.post.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.wemingle.core.domain.post.entity.posttype.PostType;
import com.wemingle.core.domain.post.vo.SaveVoteVo;
import jakarta.validation.constraints.NotBlank;
import lombok.*;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.UUID;

public class TeamPostDto {
    @Getter
    @Setter
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
        @JsonProperty(value = "isBookmarked")
        private boolean isBookmarked;
        @JsonProperty(value = "isWriter")
        private boolean isWriter;
        private VoteInfo voteInfo;

        @Builder
        public ResponseTeamPostsInfoWithMember(String teamName, String title, String content, String nickname, LocalDateTime createdTime, List<String> teamPostImgUrls, int likeCnt, int replyCnt, boolean isBookmarked, boolean isWriter, VoteInfo voteInfo) {
            this.teamName = teamName;
            this.title = title;
            this.content = content;
            this.nickname = nickname;
            this.createdTime = createdTime;
            this.teamPostImgUrls = teamPostImgUrls;
            this.likeCnt = likeCnt;
            this.replyCnt = replyCnt;
            this.isBookmarked = isBookmarked;
            this.isWriter = isWriter;
            this.voteInfo = voteInfo;
        }
    }

    @Getter
    @NoArgsConstructor
    public static class ResponseTeamPostsInfoWithTeam {
        private boolean hasWritePermission;
        private String teamName;
        HashMap<Long, TeamPostInfo> teamPostsInfo;

        @Builder
        public ResponseTeamPostsInfoWithTeam(boolean hasWritePermission, String teamName, HashMap<Long, TeamPostInfo> teamPostsInfo) {
            this.hasWritePermission = hasWritePermission;
            this.teamName = teamName;
            this.teamPostsInfo = teamPostsInfo;
        }
    }

    @Getter
    @Setter
    @NoArgsConstructor
    public static class TeamPostInfo {
        private String title;
        private String content;
        private String nickname;
        private LocalDateTime createdTime;
        private List<String> teamPostImgUrls;
        private PostType postType;
        private int likeCnt;
        private int replyCnt;
        @JsonProperty(value = "isBookmarked")
        private boolean isBookmarked;
        @JsonProperty(value = "isWriter")
        private boolean isWriter;
        private VoteInfo voteInfo;

        @Builder
        public TeamPostInfo(String title, String content, String nickname, LocalDateTime createdTime, List<String> teamPostImgUrls, PostType postType, int likeCnt, int replyCnt, boolean isBookmarked, VoteInfo voteInfo, boolean isWriter) {
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
            this.isWriter = isWriter;
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

    @Getter
    @NoArgsConstructor
    @ToString
    public static class RequestTeamPostSave {
        private Long teamPk;
        @NotBlank(message = "글의 제목은 필수입니다.")
        private String postTitle;
        @NotBlank(message = "글의 내용은 필수입니다.")
        private String postContent;
        @NotBlank(message = "투표 제목은 필수입니다.")
        private String voteTitle;
        private LocalDateTime expiryTime;
        private List<String> voteOptions;
        private List<UUID> imgIds;
        private PostType postType;
        private boolean hasVote;
        private int voteLimit;
        private boolean hasMultiVoting;
        private boolean hasAnonymousVoting;
        private boolean commentAllow;
        private boolean likeAllow;

        public SaveVoteVo of(){
            return SaveVoteVo.builder()
                    .hasVote(hasVote)
                    .title(voteTitle)
                    .voteOptions(voteOptions)
                    .expiryTime(expiryTime)
                    .voteLimit(voteLimit)
                    .isAnonymousVoting(hasAnonymousVoting)
                    .isMultiVoting(hasMultiVoting)
                    .build();
        }
    }
}