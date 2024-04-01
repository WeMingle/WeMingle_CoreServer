package com.wemingle.core.domain.post.entity;

import com.wemingle.core.domain.post.entity.area.AreaName;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class MatchingPostArea {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "pk")
    private Long pk;

    @NotNull
    @Enumerated(EnumType.STRING)
    @Column(name = "AREA_NAME")
    private AreaName areaName; // 지역시 이름

    @NotNull
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "MATCHING_POST")
    private MatchingPost matchingPost;

    @Builder
    public MatchingPostArea(AreaName areaName, MatchingPost matchingPost) {
        this.areaName = areaName;
        this.matchingPost = matchingPost;
    }
}
