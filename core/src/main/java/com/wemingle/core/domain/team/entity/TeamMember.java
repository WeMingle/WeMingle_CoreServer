package com.wemingle.core.domain.team.entity;

import com.wemingle.core.domain.common.entity.BaseEntity;
import com.wemingle.core.domain.member.entity.Member;
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
public class TeamMember extends BaseEntity {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "PK")
    private Long pk;

    @NotNull
    @Column(name = "NICKNAME", columnDefinition = "VARBINARY(255) NOT NULL")
    private String nickname;

    @NotNull
    @Column(name = "PROFILE_IMG")
    private UUID profileImg;

    @NotNull
    @Enumerated(EnumType.STRING)
    private TeamRole teamRole;

    @NotNull
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "MEMBER")
    private Member member;

    @NotNull
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "TEAM")
    private Team team;

    @Builder
    public TeamMember(String nickname, UUID profileImg, TeamRole teamRole, Member member, Team team) {
        this.nickname = nickname;
        this.profileImg = profileImg;
        this.teamRole = teamRole;
        this.member = member;
        this.team = team;
    }

    public boolean isManager() {
        return this.teamRole.equals(TeamRole.OWNER) || this.teamRole.equals(TeamRole.MANAGER);
    }
    public void updateNickname(String nickname) {
        this.nickname = nickname;
    }
    public void demoteManagerRole() {
        this.teamRole = TeamRole.PARTICIPANT;
    }
    public boolean isOwner() {
        return this.teamRole.equals(TeamRole.OWNER);
    }
    public void promoteParticipantRole() {
        this.teamRole = TeamRole.MANAGER;
    }
    public void block() {
        this.teamRole = TeamRole.BLOCKED_USER;
    }
    public boolean isMe(Member requester) {
        return this.member.equals(requester);
    }
}
