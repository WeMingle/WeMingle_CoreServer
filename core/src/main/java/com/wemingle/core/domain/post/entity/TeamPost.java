package com.wemingle.core.domain.post.entity;

import com.wemingle.core.domain.common.entity.BaseEntity;
import com.wemingle.core.domain.img.entity.TeamPostImg;
import com.wemingle.core.domain.post.entity.posttype.PostType;
import com.wemingle.core.domain.team.entity.Team;
import com.wemingle.core.domain.team.entity.TeamMember;
import com.wemingle.core.domain.vote.entity.TeamPostVote;
import com.wemingle.core.global.exceptionmessage.ExceptionMessage;
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
    @Column(name = "TITLE", length = 1000)
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
    @Column(name = "COMMENT_ALLOW")
    private boolean commentAllow;

    @NotNull
    @Column(name = "LIKE_ALLOW")
    private boolean likeAllow;

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

    @OneToOne(mappedBy = "teamPost", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.EAGER)
    private TeamPostVote teamPostVote;

    @NotNull
    @OneToMany(mappedBy = "teamPost", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<TeamPostImg> teamPostImgs;

    @Builder
    public TeamPost(String title, String content, boolean commentAllow, boolean likeAllow, PostType postType, Team team, TeamMember writer) {
        this.title = title;
        this.content = content;
        this.likeCount = 0;
        this.replyCount = 0;
        this.complaintsCount = 0;
        this.commentAllow = commentAllow;
        this.likeAllow = likeAllow;
        this.postType = postType;
        this.team = team;
        this.writer = writer;
    }

    public void addTeamPostImgs(List<TeamPostImg> teamPostImgs){
        this.teamPostImgs = teamPostImgs;
    }

    public void addTeamPostVote(TeamPostVote teamPostVote){
        this.teamPostVote = teamPostVote;
    }
    public boolean isWriter(TeamMember teamMember){
        return this.writer.equals(teamMember);
    }
    public void addLikeCnt(){
        this.likeCount += 1;
    }
    public void reduceLikeCnt(){
        if (this.likeCount == 0) throw new RuntimeException(ExceptionMessage.LIKE_CNT_LESS_THAN_ZERO.getExceptionMessage());
        this.likeCount -= 1;
    }
    public void addReplyCnt(){
        this.replyCount += 1;
    }
    public void reduceReplyCnt(){
        if (this.replyCount == 0) throw new RuntimeException(ExceptionMessage.LIKE_CNT_LESS_THAN_ZERO.getExceptionMessage());
        this.replyCount -= 1;
    }
}
