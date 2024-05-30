package com.wemingle.core.domain.vote.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.List;

@Entity
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Getter
public class VoteOption {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "PK")
    private Long pk;

    @NotNull
    @Column(name = "OPTION_NAME")
    private String optionName;

    @NotNull
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "TEAM_POST_VOTE")
    private TeamPostVote teamPostVote;

    @OneToMany(mappedBy = "voteOption", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<VoteResult> voteResults  = new ArrayList<>();

    @Builder
    public VoteOption(String optionName, TeamPostVote teamPostVote) {
        this.optionName = optionName;
        this.teamPostVote = teamPostVote;
    }

    public void addVoteResult(VoteResult voteResult) {
        this.voteResults.add(voteResult);
    }
    public void removeVoteResult(VoteResult voteResult) {
        this.voteResults.remove(voteResult);
    }
}
