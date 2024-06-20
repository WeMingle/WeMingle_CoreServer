package com.wemingle.core.domain.vote.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.List;

public class VoteDto {
    @Getter
    @Setter
    @NoArgsConstructor
    public static class ResponseExpiredVoteInfo {
        private Long teamPostVotePk;
        private String title;
        private LocalDateTime expiryTime;
        private long totalParticipantCnt;
        private List<VoteResultInfo> voteResultInfos;

        @Builder
        public ResponseExpiredVoteInfo(Long teamPostVotePk, String title, LocalDateTime expiryTime, long totalParticipantCnt, List<VoteResultInfo> voteResultInfos) {
            this.teamPostVotePk = teamPostVotePk;
            this.title = title;
            this.expiryTime = expiryTime;
            this.totalParticipantCnt = totalParticipantCnt;
            this.voteResultInfos = voteResultInfos;
        }
    }

    @Getter
    @Setter
    @NoArgsConstructor
    public static class VoteResultInfo {
        private String optionName;
        private int participantCnt;
        private List<Long> teamMemberPks;

        @Builder
        public VoteResultInfo(String optionName, int participantCnt, List<Long> teamMemberPks) {
            this.optionName = optionName;
            this.participantCnt = participantCnt;
            this.teamMemberPks = teamMemberPks;
        }
    }

    @Getter
    @Setter
    @NoArgsConstructor
    public static class ResponseVoteResult {
        private String title;
        private LocalDateTime expiryTime;
        @JsonProperty(value = "isAnonymousVoting")
        private boolean isAnonymousVoting;
        private List<VoteOptionResult> voteOptionResults;

        @Builder
        public ResponseVoteResult(String title, LocalDateTime expiryTime, boolean isAnonymousVoting, List<VoteOptionResult> voteOptionResults) {
            this.title = title;
            this.expiryTime = expiryTime;
            this.isAnonymousVoting = isAnonymousVoting;
            this.voteOptionResults = voteOptionResults;
        }
    }

    @Getter
    @Setter
    @NoArgsConstructor
    public static class VoteOptionResult {
        private String optionName;
        private long totalCnt;
        private List<TeamMemberInfo> teamMemberInfo;

        @Builder
        public VoteOptionResult(String optionName, long totalCnt, List<TeamMemberInfo> teamMemberInfo) {
            this.optionName = optionName;
            this.totalCnt = totalCnt;
            this.teamMemberInfo = teamMemberInfo;
        }
    }

    @Getter
    @Setter
    @NoArgsConstructor
    public static class TeamMemberInfo {
        private Long teamMemberPk;
        private String nickname;
        private String imgUrl;

        @Builder
        public TeamMemberInfo(Long teamMemberPk, String nickname, String imgUrl) {
            this.teamMemberPk = teamMemberPk;
            this.nickname = nickname;
            this.imgUrl = imgUrl;
        }
    }

    @Getter
    @NoArgsConstructor
    public static class RequestVote {
        private Long votePk;
        private List<Long> removeVoteResult;
        private List<Long> saveVoteResult;

        public int calculateTotalCnt(){
            return this.saveVoteResult.size() - this.removeVoteResult.size();
        }

        public boolean isExistRemoveVoteResult() {
            return this.getRemoveVoteResult() != null && !this.getRemoveVoteResult().isEmpty();
        }

        public boolean isExistSaveVoteResult() {
            return this.getSaveVoteResult() != null && !this.getSaveVoteResult().isEmpty();
        }
    }
}
