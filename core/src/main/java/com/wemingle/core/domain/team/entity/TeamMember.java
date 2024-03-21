package com.wemingle.core.domain.team.entity;

import com.wemingle.core.domain.common.entity.BaseEntity;
import com.wemingle.core.domain.team.entity.teamrole.TeamRole;
import com.wemingle.core.domain.member.entity.Member;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;

@Getter
@Entity
public class TeamMember extends BaseEntity {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "PK")
    private Long pk;

    @NotNull
    @Column(name = "NICKNAME", columnDefinition = "VARBINARY(255) NOT NULL")
    private String nickname;

    @NotNull
    @Column(name = "PROFILE_IMG")
    private String profileImg;

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
}
