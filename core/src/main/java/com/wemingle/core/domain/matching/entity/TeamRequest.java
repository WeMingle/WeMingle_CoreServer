package com.wemingle.core.domain.matching.entity;

import com.wemingle.core.domain.common.entity.BaseEntity;
import com.wemingle.core.domain.member.entity.Member;
import com.wemingle.core.domain.team.entity.Team;
import com.wemingle.core.domain.team.entity.TeamMember;
import com.wemingle.core.domain.team.entity.teamrole.TeamRole;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.UUID;

@Getter
@Entity
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class TeamRequest extends BaseEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "pk")
    private Long pk;

    @NotNull
    @Column(name = "NICKNAME", columnDefinition = "VARBINARY(255) NOT NULL")
    private String nickname;

    @NotNull
    @Column(name = "PROFILE_IMG")
    private UUID profileImg;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "TEAM")
    private Team team;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "MEMBER")
    private Member requester;

    @Builder
    public TeamRequest(String nickname, Team team, Member requester) {
        this.nickname = nickname;
        this.profileImg = UUID.randomUUID();
        this.team = team;
        this.requester = requester;
    }

    public void addTeamMemberInTeam() {
        this.getTeam().getTeamMembers().add(TeamMember.builder()
                .team(this.team)
                .member(this.requester)
                .nickname(this.nickname)
                .profileImg(this.profileImg)
                .teamRole(TeamRole.PARTICIPANT)
                .build());
    }
}
