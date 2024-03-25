package com.wemingle.core.domain.category.sports.dto;


import com.wemingle.core.domain.category.sports.entity.sportstype.SportsType;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.List;


public class OnboardDto {
    @Getter
    @NoArgsConstructor
    public static class RequestOnboardInfoDto{
        private List<SportsType> selectedSports;
    }
}
