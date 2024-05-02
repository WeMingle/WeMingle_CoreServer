package com.wemingle.core.domain.post.repository;

import com.wemingle.core.domain.category.sports.entity.sportstype.SportsType;
import com.wemingle.core.domain.member.entity.Member;
import com.wemingle.core.domain.post.dto.sortoption.SortOption;
import com.wemingle.core.domain.post.entity.MatchingPost;
import com.wemingle.core.domain.post.entity.abillity.Ability;
import com.wemingle.core.domain.post.entity.area.AreaName;
import com.wemingle.core.domain.post.entity.gender.Gender;
import com.wemingle.core.domain.post.entity.recruitertype.RecruiterType;
import com.wemingle.core.domain.team.entity.recruitmenttype.RecruitmentType;
import org.springframework.data.domain.Pageable;

import java.time.LocalDate;
import java.time.YearMonth;
import java.util.List;

public interface DSLMatchingPostRepository {

    List<MatchingPost> findFilteredMatchingPostByCalendar(Long lastIdx, RecruitmentType recruitmentType, Ability ability, Gender gender, RecruiterType recruiterType, List<AreaName> areaList, LocalDate currentDate, LocalDate dateFilter, YearMonth monthFilter, SortOption sortOption, LocalDate lastExpiredDate, SportsType sportsType, Pageable pageable);
    List<MatchingPost> findFilteredMatchingPostByMap(Long lastIdx, RecruitmentType recruitmentType, Ability ability, Gender gender, RecruiterType recruiterType, LocalDate currentDate, List<LocalDate> dateFilter, YearMonth monthFilter, SortOption sortOption, LocalDate lastExpiredDate, SportsType sportsType, double topLat, double bottomLat, double leftLon, double rightLon, boolean excludeRegionUnit, Pageable pageable);
    List<MatchingPost> findFilteredMatchingPostByMapDetail(RecruitmentType recruitmentType, Ability ability, Gender gender, RecruiterType recruiterType, LocalDate currentDate, List<LocalDate> endDateFilter, YearMonth monthFilter, LocalDate lastExpiredDate, SportsType sportsType, double topLat, double bottomLat, double leftLon, double rightLon, boolean excludeRegionUnit);
    Integer findFilteredMatchingPostCnt(RecruitmentType recruitmentType, Ability ability, Gender gender, RecruiterType recruiterType, List<AreaName> areaList, LocalDate currentDate, LocalDate dateFilter, YearMonth monthFilter, SportsType sportsType);
    Integer findFilteredMatchingPostByMapCnt(RecruitmentType recruitmentType, Ability ability, Gender gender, RecruiterType recruiterType, LocalDate currentDate, List<LocalDate> dateFilter, YearMonth monthFilter, Boolean excludeExpired, LocalDate lastExpiredDate, SportsType sportsType, double topLat, double bottomLat, double leftLon, double rightLon, boolean excludeRegionUnit);
    List<MatchingPost> findCompletedMatchingPosts(Long nextIdx, RecruiterType recruiterType, boolean excludeCompleteMatchesFilter, Member member, List<MatchingPost> matchingPostWithReview);
    List<MatchingPost> findMatchingPostInMap(double topLat, double bottomLat, double leftLon, double rightLon);
    List<MatchingPost> findMyAllMatchingPosts(Long nextIdx, RecruiterType recruiterType, String memberId);
}
