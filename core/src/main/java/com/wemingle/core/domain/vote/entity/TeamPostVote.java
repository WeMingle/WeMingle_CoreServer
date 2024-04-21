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

    @NotNull
    @Column(name = "EXPIRY_TIME")
    private LocalDateTime expiryTime;

    @NotNull
    @Column(name = "IS_MULTI_VOTING")
    private boolean isMultiVoting;

    @NotNull
    @Column(name = "IS_ANONYMOUS_VOTING")
    private boolean isAnonymousVoting;

    @NotNull
    @Column(name = "IS_ADDITIONAL_ITEM")
    private boolean isAdditionalItem;

    @NotNull
    @OneToOne(mappedBy = "teamPostVote", cascade = CascadeType.ALL, orphanRemoval = true)
    private TeamPost TeamPost;

    @NotNull
    @OneToMany(mappedBy = "teamPostVote", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<VoteOption> voteOptions = new ArrayList<>();

    @Builder
    public TeamPostVote(String title, LocalDateTime expiryTime, boolean isMultiVoting, boolean isAnonymousVoting, boolean isAdditionalItem, List<VoteOption> voteOptions) {
        this.title = title;
        this.expiryTime = expiryTime;
        this.isMultiVoting = isMultiVoting;
        this.isAnonymousVoting = isAnonymousVoting;
        this.isAdditionalItem = isAdditionalItem;
        this.voteOptions = voteOptions;
    }
}
