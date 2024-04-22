package com.wemingle.core.domain.post.dto;

import com.wemingle.core.domain.post.entity.posttype.PostType;
import com.wemingle.core.domain.post.vo.SaveVoteVo;
import jakarta.validation.constraints.NotBlank;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

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