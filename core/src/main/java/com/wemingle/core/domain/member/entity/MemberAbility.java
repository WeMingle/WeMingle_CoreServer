package com.wemingle.core.domain.member.entity;

import com.wemingle.core.domain.category.sports.entity.sportstype.SportsType;
import com.wemingle.core.domain.common.entity.BaseEntity;
import com.wemingle.core.domain.post.entity.abillity.Ability;
import jakarta.persistence.*;
import lombok.*;

@Getter
@Setter
@Entity
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class MemberAbility extends BaseEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "pk")
    private Long pk;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "MEMBER")
    private Member member;

    @Enumerated(EnumType.STRING)
    private Ability ability;

    @Enumerated(EnumType.STRING)
    private SportsType sportsType;

    @Builder
    public MemberAbility(Member member, Ability ability, SportsType sportsType) {
        this.member = member;
        this.ability = ability;
        this.sportsType = sportsType;
    }
}
