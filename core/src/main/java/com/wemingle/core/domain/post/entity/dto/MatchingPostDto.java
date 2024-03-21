package com.wemingle.core.domain.post.entity.dto;

import com.wemingle.core.domain.post.entity.abillity.Ability;
import com.wemingle.core.domain.post.entity.area.AreaName;
import com.wemingle.core.domain.post.entity.recruitertype.RecruiterType;
import lombok.Builder;
import lombok.Setter;

import java.time.LocalDate;


public class MatchingPostDto {
    @Setter
    public static class ResponseMatchingPostDto{
        private String profilePicUrl;
        private String writer;
        private String contents;
        private AreaName areaName;
        private int matchingCnt;
        private LocalDate matchingDate;
        private RecruiterType recruiterType;
        private Ability ability;
        private boolean isLocationConsensusPossible;

        @Builder
        public ResponseMatchingPostDto(String profilePicUrl, String writer, String contents, AreaName areaName, int matchingCnt, LocalDate matchingDate, RecruiterType recruiterType, Ability ability, boolean isLocationConsensusPossible) {
            this.profilePicUrl = profilePicUrl;
            this.writer = writer;
            this.contents = contents;
            this.areaName = areaName;
            this.matchingCnt = matchingCnt;
            this.matchingDate = matchingDate;
            this.recruiterType = recruiterType;
            this.ability = ability;
            this.isLocationConsensusPossible = isLocationConsensusPossible;
        }
    }
}
