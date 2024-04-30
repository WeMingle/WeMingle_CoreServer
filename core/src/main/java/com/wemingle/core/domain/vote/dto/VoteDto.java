package com.wemingle.core.domain.vote.dto;

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

}
