package com.wemingle.core.domain.category.sports.dto;


import com.wemingle.core.domain.category.sports.entity.sportstype.Sportstype;
import lombok.Value;

import java.util.List;

public class OnboardDto {
    @Value
    public static class RequestOnboardInfoDto{
        List<Sportstype> selectedSports;
    }
}
