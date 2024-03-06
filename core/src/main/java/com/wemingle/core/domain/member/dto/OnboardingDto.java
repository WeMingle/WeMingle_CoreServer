package com.wemingle.core.domain.member.dto;

import com.wemingle.core.domain.category.sports.entity.sportstype.Sportstype;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.List;

@Getter
@NoArgsConstructor
public class OnboardingDto {
    List<Sportstype> favoriteSports;
}
