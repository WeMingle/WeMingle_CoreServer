package com.wemingle.core.domain.post.dto;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@ToString
@Setter
@Getter
public class MatchingPostMapDto {
    private final double lat;
    private final double lon;
    private final int cnt;

    @Builder
    public MatchingPostMapDto(double lat, double lon, int cnt) {
        this.lat = lat;
        this.lon = lon;
        this.cnt = cnt;
    }
}
