package com.wemingle.core.domain.post.entity;

import com.wemingle.core.domain.common.entity.BaseEntity;
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
public class MatchingPostMatchingDate extends BaseEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "pk")
    private Long pk;

    @NotNull
    @ManyToOne
    @JoinColumn(name = "MATCHING_POST")
    private MatchingPost matchingPost;

    @NotNull
    @Column(name = "MATCHING_DATE")
    private LocalDate matchingDate;

    @Builder
    public MatchingPostMatchingDate(MatchingPost matchingPost, LocalDate matchingDate) {
        this.matchingPost = matchingPost;
        this.matchingDate = matchingDate;
    }
}
