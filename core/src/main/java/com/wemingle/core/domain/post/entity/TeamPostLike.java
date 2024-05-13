package com.wemingle.core.domain.post.entity;

import com.wemingle.core.domain.common.entity.BaseEntity;
import com.wemingle.core.domain.team.entity.TeamMember;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class TeamPostLike extends BaseEntity {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "PK")
    private Long pk;

    @Column(name = "IS_DELETED")
    private boolean isDeleted;

    @NotNull
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "TEAM_POST")
    private TeamPost teamPost;

    @NotNull
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "TEAM_MEMBER")
    private TeamMember teamMember;

    public void restore(){
        this.isDeleted = false;
    }

    public void delete(){
        this.isDeleted = true;
    }

    @Builder
    public TeamPostLike(TeamPost teamPost, TeamMember teamMember) {
        this.teamPost = teamPost;
        this.teamMember = teamMember;
    }
}
