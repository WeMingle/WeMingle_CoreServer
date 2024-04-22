package com.wemingle.core.domain.post.vo;

import lombok.Builder;
import lombok.Getter;

import java.time.LocalDateTime;
import java.util.List;

@Getter
public class SaveVoteVo {
    private final boolean hasVote;
    private final String title;
    private final List<String> voteOptions;
    private final LocalDateTime expiryTime;
    private final int voteLimit;
    private final boolean isMultiVoting;
    private final boolean isAnonymousVoting;

    @Builder
    public SaveVoteVo(boolean hasVote, String title, List<String> voteOptions, LocalDateTime expiryTime, int voteLimit, boolean isMultiVoting, boolean isAnonymousVoting) {
        this.hasVote = hasVote;
        this.title = title;
        this.voteOptions = voteOptions;
        this.expiryTime = expiryTime;
        this.voteLimit = voteLimit;
        this.isMultiVoting = isMultiVoting;
        this.isAnonymousVoting = isAnonymousVoting;
    }
}
