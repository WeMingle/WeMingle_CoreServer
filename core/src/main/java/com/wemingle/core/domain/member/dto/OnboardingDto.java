package com.wemingle.core.domain.member.dto;

import com.wemingle.core.domain.category.sports.entity.sportstype.SportsType;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.List;

@Getter
@NoArgsConstructor
public class OnboardingDto {
    List<SportsType> favoriteSports;
}
