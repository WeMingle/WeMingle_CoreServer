package com.wemingle.core.domain.member.entity;

import com.wemingle.core.domain.team.entity.Team;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Getter
@Entity
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class BannedTeamMember {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "pk")
    private Long pk;

    @NotNull
    @Column(name = "BANNED_DATE")
    private LocalDate bannedDate;

    @NotNull
    @Column(name = "BAN_END_DATE")
    private LocalDate banEndDate;

    @NotNull
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "BANNED_MEMBER")
    private Member bannedMember;

    @NotNull
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "TEAM")
    private Team team;

    @Builder
    public BannedTeamMember(Member bannedMember, Team team) {
        this.bannedDate = LocalDate.now();
        this.banEndDate = LocalDate.now().plusMonths(1);
        this.bannedMember = bannedMember;
        this.team = team;
    }
}
