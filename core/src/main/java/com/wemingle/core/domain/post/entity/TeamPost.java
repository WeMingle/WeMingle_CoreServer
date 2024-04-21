package com.wemingle.core.domain.post.entity;

import com.wemingle.core.domain.common.entity.BaseEntity;
import com.wemingle.core.domain.img.entity.TeamPostImg;
import com.wemingle.core.domain.post.entity.posttype.PostType;
import com.wemingle.core.domain.team.entity.Team;
import com.wemingle.core.domain.team.entity.TeamMember;
import com.wemingle.core.domain.vote.entity.TeamPostVote;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.List;

@Entity
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Getter
public class TeamPost extends BaseEntity {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "PK")
    private Long pk;

    @NotNull
    @Column(name = "TITLE", length = 3000)
    private String title;

    @NotNull
    @Column(name = "CONTENT", length = 3000)
    private String content;

    @NotNull
    @Column(name = "LIKE_COUNT")
    private int likeCount;

    @NotNull
    @Column(name = "REPLY_COUNT")
    private int replyCount;

    @NotNull
    @Column(name = "COMPLAINTS_COUNT")
    private int complaintsCount;

    @NotNull
    @Enumerated(EnumType.STRING)
    private PostType postType;

    @NotNull
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "TEAM")
    private Team team;

    @NotNull
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "WRITER")
    private TeamMember writer;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "TEAM_POST_VOTE")
    private TeamPostVote teamPostVote;

    @NotNull
    @OneToMany(mappedBy = "teamPost", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<TeamPostImg> teamPostImgs;

    @Builder
    public TeamPost(String title, String content, int likeCount, int replyCount, int complaintsCount, PostType postType, Team team, TeamMember writer, TeamPostVote teamPostVote) {
        this.title = title;
        this.content = content;
        this.likeCount = likeCount;
        this.replyCount = replyCount;
        this.complaintsCount = complaintsCount;
        this.postType = postType;
        this.team = team;
        this.writer = writer;
        this.teamPostVote = teamPostVote;
    }
}
