package com.wemingle.core.domain.comment.entity;

import com.wemingle.core.domain.common.entity.BaseEntity;
import com.wemingle.core.domain.team.entity.TeamMember;
import com.wemingle.core.domain.post.entity.TeamPost;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Comment extends BaseEntity {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "PK")
    private Long pk;

    @NotNull
    @Column(name = "CONTENT", length = 3000)
    private String content;

    @NotNull
    @Column(name = "IS_DELETED")
    private boolean isDeleted;

    @NotNull
    @Column(name = "IS_LOCKED")
    private boolean isLocked;

    @NotNull
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "TEAM_POST")
    private TeamPost teamPost;

    @NotNull
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "WRITER")
    private TeamMember writer;

    @Builder
    public Comment(String content, TeamPost teamPost, TeamMember writer) {
        this.content = content;
        this.teamPost = teamPost;
        this.writer = writer;
    }

    public void updateContent(String content){
        this.content = content;
    }

    public void delete(){
        this.isDeleted = true;
        this.content = "삭제된 댓글입니다";
    }

    public boolean isWriter(TeamMember requester){
        return this.writer.equals(requester);
    }
    public void updateByWithdrawMember() {
        this.content = "탈퇴된 회원이 작성한 댓글입니다";
        this.isDeleted = true;
    }
}
