package com.wemingle.core.domain.comment.entity;

import com.wemingle.core.domain.common.entity.BaseEntity;
import com.wemingle.core.domain.team.entity.TeamMember;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.NoArgsConstructor;

@Entity
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Reply extends BaseEntity {
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
    @Column(name = "COMPLAINTS_COUNT")
    private int complaintsCount;

    @NotNull
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "COMMENT")
    private Comment comment;

    @NotNull
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "TEAM_MEMBER")
    private TeamMember teamMember;

    @Builder
    public Reply(String content, Comment comment, TeamMember teamMember) {
        this.content = content;
        this.comment = comment;
        this.teamMember = teamMember;
    }
}
