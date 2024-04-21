package com.wemingle.core.domain.member.entity;

import com.wemingle.core.domain.category.sports.entity.sportstype.SportsType;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class MemberPreferenceSports {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "PK", nullable = false)
    private Long pk;

    @NotNull
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "MEMBER")
    private Member member;

    @Getter
    @NotNull
    @Enumerated(EnumType.STRING)
    private SportsType sports;

    @Builder
    public MemberPreferenceSports(Member member, SportsType sports) {
        this.member = member;
        this.sports = sports;
    }
}