package com.wemingle.core.domain.matching.entity;

import com.wemingle.core.domain.common.entity.BaseEntity;
import com.wemingle.core.domain.member.entity.Member;
import com.wemingle.core.domain.post.entity.MatchingPost;
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
public class MatchingRequest extends BaseEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "pk")
    private Long pk;

    @Column(name = "CONTENT", length = 3000)
    private String content;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "TEAM")
    private Team team;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "MEMBER")
    private Member member;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "MATCHING_POST")
    private MatchingPost matchingPost;

    @Enumerated(EnumType.STRING)
    @Column(name = "MATCHING_REQUEST_STATUS")
    private MatchingStatus matchingRequestStatus;

    @Builder
    public MatchingRequest(String content, Team team, Member member, MatchingPost matchingPost) {
        this.content = content;
        this.team = team;
        this.member = member;
        this.matchingPost = matchingPost;
        this.matchingRequestStatus = MatchingStatus.PENDING;
    }

    public void cancelRequest(){
        this.matchingRequestStatus = MatchingStatus.CANCEL;
    }

    public void completeRequest(){
        this.matchingRequestStatus = MatchingStatus.COMPLETE;
    }

    public Matching of(MatchingRequest matchingRequest){
        return Matching.builder()
                .matchingPost(matchingRequest.getMatchingPost())
                .member(matchingRequest.getMember())
                .team(matchingRequest.getTeam())
                .build();
    }
}
