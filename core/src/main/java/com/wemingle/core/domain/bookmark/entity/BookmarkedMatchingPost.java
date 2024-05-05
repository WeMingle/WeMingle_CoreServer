package com.wemingle.core.domain.bookmark.entity;

import com.wemingle.core.domain.common.entity.BaseEntity;
import com.wemingle.core.domain.member.entity.Member;
import com.wemingle.core.domain.post.entity.MatchingPost;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Entity
public class BookmarkedMatchingPost extends BaseEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "pk")
    private Long pk;

    @NotNull
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "MATCHING_POST")
    private MatchingPost matchingPost;

    @NotNull
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "MEMBER")
    private Member member;

    @Builder
    public BookmarkedMatchingPost(Long pk, MatchingPost matchingPost, Member member) {
        this.pk = pk;
        this.matchingPost = matchingPost;
        this.member = member;
    }

    public void updateMatchingPost(MatchingPost matchingPost){
        this.matchingPost = matchingPost;
    }
}
