package com.wemingle.core.domain.matching.entity;

import com.wemingle.core.domain.common.entity.BaseEntity;
import com.wemingle.core.domain.post.entity.MatchingPost;
import com.wemingle.core.domain.team.entity.TeamMember;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;

@Entity
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class MatchingRequest extends BaseEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "pk")
    private Long pk;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "TEAM_MEMBER")
    private TeamMember teamMember;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "MATCHIN_POST")
    private MatchingPost matchingPost;
}
