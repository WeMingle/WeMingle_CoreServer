package com.wemingle.core.domain.matching.entity;

import com.wemingle.core.domain.member.entity.Member;
import com.wemingle.core.domain.post.entity.matchingstatus.MatchingStatus;
import com.wemingle.core.domain.team.entity.Team;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Entity
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class TeamRequest {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "pk")
    private Long pk;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "TEAM")
    private Team team;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "MEMBER")
    private Member requester;

    @Enumerated(EnumType.STRING)
    @Column(name = "TEAM_REQUEST_STATUS")
    private MatchingStatus teamRequestStatus;

    @Builder
    public TeamRequest(Team team, Member requester, MatchingStatus teamRequestStatus) {
        this.team = team;
        this.requester = requester;
        this.teamRequestStatus = teamRequestStatus;
    }
}
