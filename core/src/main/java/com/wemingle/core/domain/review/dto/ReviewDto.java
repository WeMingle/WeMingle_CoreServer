package com.wemingle.core.domain.review.dto;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
public class ReviewDto {
    private Long pk;
    private int rating;
    private String content;
    private String reviewer;
    private LocalDateTime createTime;

    @Builder
    public ReviewDto(Long pk, int rating, String content, String reviewer, LocalDateTime createTime) {
        this.pk = pk;
        this.rating = rating;
        this.content = content;
        this.reviewer = reviewer;
        this.createTime = createTime;
    }
}
