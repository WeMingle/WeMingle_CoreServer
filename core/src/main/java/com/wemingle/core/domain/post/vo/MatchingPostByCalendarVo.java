package com.wemingle.core.domain.post.vo;

import com.wemingle.core.domain.category.sports.entity.sportstype.SportsType;
import com.wemingle.core.domain.post.dto.MatchingPostDto;
import com.wemingle.core.domain.post.dto.sortoption.SortOption;
import com.wemingle.core.domain.post.entity.abillity.Ability;
import com.wemingle.core.domain.post.entity.area.AreaName;
import com.wemingle.core.domain.post.entity.gender.Gender;
import com.wemingle.core.domain.post.entity.recruitertype.RecruiterType;
import com.wemingle.core.domain.team.entity.recruitmenttype.RecruitmentType;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;
import java.time.YearMonth;
import java.util.List;

@Getter
@Setter
public class MatchingPostByCalendarVo {
    private Long lastIdx;
    private RecruitmentType recruitmentType;
    private Ability ability;
    private Gender gender;
    private RecruiterType recruiterType;
    private List<AreaName> areaList;
    private LocalDate dateFilter;
    private YearMonth monthFilter;
    private Boolean excludeExpired;
    private SortOption sortOption;
    private LocalDate lastExpiredDate;
    private Integer callCnt;
    private SportsType sportsType;

    public MatchingPostByCalendarVo(MatchingPostDto.RequestCalendarDto requestCalendarDto) {
        this.lastIdx = requestCalendarDto.getLastIdx();
        this.recruitmentType = requestCalendarDto.getRecruitmentType();
        this.ability = requestCalendarDto.getAbility();
        this.gender = requestCalendarDto.getGender();
        this.recruiterType = requestCalendarDto.getRecruiterType();
        this.areaList = requestCalendarDto.getAreaList();
        this.dateFilter = requestCalendarDto.getDateFilter();
        this.monthFilter = requestCalendarDto.getMonthFilter();
        this.excludeExpired = requestCalendarDto.getExcludeExpired();
        this.sortOption = requestCalendarDto.getSortOption();
        this.lastExpiredDate = requestCalendarDto.getLastExpiredDate();
        this.callCnt = requestCalendarDto.getCallCnt();
        this.sportsType = requestCalendarDto.getSportsType();
    }
}
