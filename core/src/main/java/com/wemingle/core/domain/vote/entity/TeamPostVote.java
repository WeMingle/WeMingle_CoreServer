package com.wemingle.core.domain.vote.entity;

import com.wemingle.core.domain.common.entity.BaseEntity;
import com.wemingle.core.domain.post.entity.TeamPost;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Getter
public class TeamPostVote extends BaseEntity {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "PK")
    private Long pk;

    @NotNull
    @Column(name = "TITLE", length = 1000)
    private String title;

    @Column(name = "EXPIRY_TIME")
    private LocalDateTime expiryTime;

    @NotNull
    @Column(name = "IS_MULTI_VOTING")
    private boolean isMultiVoting;

    @NotNull
    @Column(name = "IS_ANONYMOUS_VOTING")
    private boolean isAnonymousVoting;

    @NotNull
    @Column(name = "VOTE_LIMIT")
    private int voteLimit;

    @NotNull
    @OneToOne(mappedBy = "teamPostVote", cascade = CascadeType.ALL, orphanRemoval = true)
    private TeamPost teamPost;

    @NotNull
    @OneToMany(mappedBy = "teamPostVote", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<VoteOption> voteOptions = new ArrayList<>();

    @Builder
    public TeamPostVote(String title, LocalDateTime expiryTime, boolean isMultiVoting, boolean isAnonymousVoting, int voteLimit, com.wemingle.core.domain.post.entity.TeamPost teamPost) {
        this.title = title;
        this.expiryTime = expiryTime;
        this.isMultiVoting = isMultiVoting;
        this.isAnonymousVoting = isAnonymousVoting;
        this.voteLimit = voteLimit;
        this.teamPost = teamPost;
    }

    public void addVoteOptions(List<VoteOption> voteOptions){
        this.voteOptions = voteOptions;
    }
}
