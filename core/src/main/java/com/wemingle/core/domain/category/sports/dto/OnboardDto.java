package com.wemingle.core.domain.category.sports.dto;


import com.wemingle.core.domain.category.sports.entity.sportstype.Sportstype;
import lombok.NoArgsConstructor;

import java.util.List;


public class OnboardDto {
    @NoArgsConstructor
    public static class RequestOnboardInfoDto{
        private List<Sportstype> selectedSports;
    }
}
