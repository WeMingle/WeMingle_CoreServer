package com.wemingle.core.domain.post.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.wemingle.core.domain.post.entity.posttype.PostType;
import com.wemingle.core.domain.post.vo.SaveVoteVo;
import com.wemingle.core.domain.vote.entity.votestatus.VoteStatus;
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
        private String imgUrl;

        @Builder
        public ResponseTeamPostsInfoWithMember(String teamName, String title, String content, String nickname, LocalDateTime createdTime, List<String> teamPostImgUrls, int likeCnt, int replyCnt, boolean isBookmarked, boolean isWriter, VoteInfo voteInfo, String imgUrl) {
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
            this.imgUrl = imgUrl;
        }
    }

    @Getter
    @NoArgsConstructor
    public static class ResponseMyAllPostDto{
        private String title;
        private String writer;
        private UUID writerPic;
        private LocalDateTime writeTime;
        private String content;
        private List<UUID> picList;
        private Integer likeCnt;
        private Integer replyCnt;
        private boolean isBookmarked;
        private VoteInfo voteInfo;

        @Builder
        public ResponseMyAllPostDto(String title, String writer, UUID writerPic, LocalDateTime writeTime, String content, List<UUID> picList, Integer likeCnt, Integer replyCnt, boolean isBookmarked, VoteInfo voteInfo) {
            this.title = title;
            this.writer = writer;
            this.writerPic = writerPic;
            this.writeTime = writeTime;
            this.content = content;
            this.picList = picList;
            this.likeCnt = likeCnt;
            this.replyCnt = replyCnt;
            this.isBookmarked = isBookmarked;
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
        private String imgUrl;

        @Builder
        public TeamPostInfo(String title, String content, String nickname, LocalDateTime createdTime, List<String> teamPostImgUrls, PostType postType, int likeCnt, int replyCnt, boolean isBookmarked, boolean isWriter, VoteInfo voteInfo, String imgUrl) {
            this.title = title;
            this.content = content;
            this.nickname = nickname;
            this.createdTime = createdTime;
            this.teamPostImgUrls = teamPostImgUrls;
            this.postType = postType;
            this.likeCnt = likeCnt;
            this.replyCnt = replyCnt;
            this.isBookmarked = isBookmarked;
            this.isWriter = isWriter;
            this.voteInfo = voteInfo;
            this.imgUrl = imgUrl;
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

    @Getter
    @Setter
    @NoArgsConstructor
    public static class ResponseSearchTeamPost {
        private String title;
        private String content;
        private String writerName;
        private LocalDateTime createTime;
        private int likeCnt;
        private int replyCnt;
        @JsonProperty(value = "isBookmarked")
        private boolean isBookmarked;
        @JsonProperty(value = "isWriter")
        private boolean isWriter;
        private String imgUrl;

        @Builder
        public ResponseSearchTeamPost(String title, String content, String writerName, LocalDateTime createTime, int likeCnt, int replyCnt, boolean isBookmarked, boolean isWriter, String imgUrl) {
            this.title = title;
            this.content = content;
            this.writerName = writerName;
            this.createTime = createTime;
            this.likeCnt = likeCnt;
            this.replyCnt = replyCnt;
            this.isBookmarked = isBookmarked;
            this.isWriter = isWriter;
            this.imgUrl = imgUrl;
        }
    }

    @Getter
    @Setter
    @NoArgsConstructor
    public static class RequestPostLike {
        private Long teamPostPk;
    }

    @Getter
    @Setter
    @NoArgsConstructor
    public static class ResponseTeamPostDetail {
        private String title;
        private String content;
        private String nickname;
        private String imgUrl;
        private LocalDateTime createdTime;
        private List<String> teamPostImgUrls;
        private int likeCnt;
        private int replyCnt;
        @JsonProperty(value = "isBookmarked")
        private boolean isBookmarked;
        @JsonProperty(value = "isWriter")
        private boolean isWriter;
        private VoteStatus voteStatus;
        private VoteInfoWithPk voteInfo;
        private List<MyVoteHistory> myVoteHistory;

        @Builder
        public ResponseTeamPostDetail(String title, String content, String nickname, String imgUrl, LocalDateTime createdTime, List<String> teamPostImgUrls, int likeCnt, int replyCnt, boolean isBookmarked, boolean isWriter, VoteStatus voteStatus, VoteInfoWithPk voteInfo, List<MyVoteHistory> myVoteHistory) {
            this.title = title;
            this.content = content;
            this.nickname = nickname;
            this.imgUrl = imgUrl;
            this.createdTime = createdTime;
            this.teamPostImgUrls = teamPostImgUrls;
            this.likeCnt = likeCnt;
            this.replyCnt = replyCnt;
            this.isBookmarked = isBookmarked;
            this.isWriter = isWriter;
            this.voteStatus = voteStatus;
            this.voteInfo = voteInfo;
            this.myVoteHistory = myVoteHistory;
        }
    }

    @Getter
    @NoArgsConstructor
    public static class VoteInfoWithPk{
        private Long votePk;
        private List<VoteOptionInfoWithPk> voteOptionInfos;

        @Builder
        public VoteInfoWithPk(Long votePk, List<VoteOptionInfoWithPk> voteOptionInfos) {
            this.votePk = votePk;
            this.voteOptionInfos = voteOptionInfos;
        }
    }

    @Getter
    @NoArgsConstructor
    public static class VoteOptionInfoWithPk{
        private Long voteOptionPk;
        private String optionName;
        private int resultCnt;

        @Builder
        public VoteOptionInfoWithPk(Long voteOptionPk, String optionName, int resultCnt) {
            this.voteOptionPk = voteOptionPk;
            this.optionName = optionName;
            this.resultCnt = resultCnt;
        }
    }

    @Getter
    @NoArgsConstructor
    public static class MyVoteHistory {
        private Long myVotePk;
        private String myVoteOption;

        @Builder
        public MyVoteHistory(Long myVotePk, String myVoteOption) {
            this.myVotePk = myVotePk;
            this.myVoteOption = myVoteOption;
        }
    }
}