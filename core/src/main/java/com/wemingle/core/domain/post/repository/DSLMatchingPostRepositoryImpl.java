package com.wemingle.core.domain.post.repository;

import com.querydsl.core.types.Order;
import com.querydsl.core.types.OrderSpecifier;
import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.jpa.impl.JPAQueryFactory;
import com.wemingle.core.domain.category.sports.entity.sportstype.SportsType;
import com.wemingle.core.domain.member.entity.Member;
import com.wemingle.core.domain.post.dto.sortoption.SortOption;
import com.wemingle.core.domain.post.entity.MatchingPost;
import com.wemingle.core.domain.post.entity.abillity.Ability;
import com.wemingle.core.domain.post.entity.area.AreaName;
import com.wemingle.core.domain.post.entity.gender.Gender;
import com.wemingle.core.domain.post.entity.locationselectiontype.LocationSelectionType;
import com.wemingle.core.domain.post.entity.matchingstatus.MatchingStatus;
import com.wemingle.core.domain.post.entity.recruitertype.RecruiterType;
import com.wemingle.core.domain.team.entity.recruitmenttype.RecruitmentType;
import com.wemingle.core.global.exceptionmessage.ExceptionMessage;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.time.YearMonth;
import java.util.List;

import static com.wemingle.core.domain.post.entity.QMatchingPost.matchingPost;

@Slf4j
@Repository
@RequiredArgsConstructor
public class DSLMatchingPostRepositoryImpl implements DSLMatchingPostRepository{
    private final JPAQueryFactory jpaQueryFactory;

    @Override
    public List<MatchingPost> findFilteredMatchingPostByCalendar(Long lastIdx,
                                                                 RecruitmentType recruitmentType,
                                                                 Ability ability,
                                                                 Gender gender,
                                                                 RecruiterType recruiterType,
                                                                 List<AreaName> areaList,
                                                                 LocalDate currentDate,
                                                                 LocalDate dateFilter,
                                                                 YearMonth monthFilter,
                                                                 SortOption sortOption,
                                                                 LocalDate lastExpiredDate,
                                                                 SportsType sportsType,
                                                                 Pageable pageable) {
        return jpaQueryFactory.selectFrom(matchingPost)
                .where(
                        lastIdxLt(lastIdx),
                        recruitmentTypeEq(recruitmentType),
                        abilityEq(ability),
                        genderEq(gender),
                        recruiterTypeEq(recruiterType),
                        areaListIn(areaList),
                        currentDateAfter(currentDate),
                        dateFilterEq(dateFilter,monthFilter),
                        lastExpiredDateLoe(lastExpiredDate),
                        sportsTypeEq(sportsType)
                )
                .offset(pageable.getOffset())
                .limit(pageable.getPageSize())
                .orderBy(getSortOption(sortOption))
                .fetch();
    }

    @Override
    public List<MatchingPost> findFilteredMatchingPostByMap(Long lastIdx, RecruitmentType recruitmentType, Ability ability, Gender gender, RecruiterType recruiterType, LocalDate currentDate, List<LocalDate> dateFilter, YearMonth monthFilter, SortOption sortOption, LocalDate lastExpiredDate, SportsType sportsType, double topLat, double bottomLat, double leftLon, double rightLon, boolean excludeRegionUnit, Pageable pageable) {
        return jpaQueryFactory.selectFrom(matchingPost)
                .where(
                        lastIdxLt(lastIdx),
                        recruitmentTypeEq(recruitmentType),
                        abilityEq(ability),
                        genderEq(gender),
                        recruiterTypeEq(recruiterType),
                        currentDateAfter(currentDate),
                        dateFilterIn(dateFilter,monthFilter),
                        lastExpiredDateLoe(lastExpiredDate),
                        sportsTypeEq(sportsType),
                        locationIn(topLat,bottomLat,leftLon,rightLon),
                        isExcludeRegionUnit(excludeRegionUnit)
                )
                .offset(pageable.getOffset())
                .limit(pageable.getPageSize())
                .orderBy(getSortOption(sortOption))
                .fetch();
    }

    @Override
    public List<MatchingPost> findFilteredMatchingPostByMapDetail(RecruitmentType recruitmentType, Ability ability, Gender gender, RecruiterType recruiterType, LocalDate currentDate, List<LocalDate> dateFilter, YearMonth monthFilter, LocalDate lastExpiredDate, SportsType sportsType, double topLat, double bottomLat, double leftLon, double rightLon, boolean excludeRegionUnit) {
        return jpaQueryFactory.selectFrom(matchingPost)
                .where(
                        recruitmentTypeEq(recruitmentType),
                        abilityEq(ability),
                        genderEq(gender),
                        recruiterTypeEq(recruiterType),
                        currentDateAfter(currentDate),
                        dateFilterIn(dateFilter,monthFilter),
                        lastExpiredDateLoe(lastExpiredDate),
                        sportsTypeEq(sportsType),
                        locationIn(topLat,bottomLat,leftLon,rightLon),
                        isExcludeRegionUnit(excludeRegionUnit)
                )
                .orderBy(matchingPost.pk.desc())
                .fetch();
    }

    @Override
    public Integer findFilteredMatchingPostCnt(RecruitmentType recruitmentType, Ability ability, Gender gender, RecruiterType recruiterType, List<AreaName> areaList, LocalDate currentDate, LocalDate dateFilter, YearMonth monthFilter, SportsType sportsType) {
        Long cnt = jpaQueryFactory.select(matchingPost.count())
                .from(matchingPost)
                .where(
                        recruitmentTypeEq(recruitmentType),
                        abilityEq(ability),
                        genderEq(gender),
                        recruiterTypeEq(recruiterType),
                        areaListIn(areaList),
                        currentDateAfter(currentDate),
                        dateFilterEq(dateFilter, monthFilter),
                        sportsTypeEq(sportsType)
                ).fetchOne();

        return cnt == null ? 0 : cnt.intValue();
    }

    @Override
    public Integer findFilteredMatchingPostByMapCnt(RecruitmentType recruitmentType, Ability ability, Gender gender, RecruiterType recruiterType, LocalDate currentDate, List<LocalDate> dateFilter, YearMonth monthFilter, Boolean excludeExpired, LocalDate lastExpiredDate, SportsType sportsType, double topLat, double bottomLat, double leftLon, double rightLon, boolean excludeRegionUnit) {
        Long cnt = jpaQueryFactory.select(matchingPost.count())
                .from(matchingPost)
                .where(
                        recruitmentTypeEq(recruitmentType),
                        abilityEq(ability),
                        genderEq(gender),
                        recruiterTypeEq(recruiterType),
                        currentDateAfter(currentDate),
                        dateFilterIn(dateFilter,monthFilter),
                        lastExpiredDateLoe(lastExpiredDate),
                        sportsTypeEq(sportsType),
                        locationIn(topLat,bottomLat,leftLon,rightLon),
                        isExcludeRegionUnit(excludeRegionUnit)
                ).fetchOne();

        return cnt == null ? 0 : cnt.intValue();
    }

    private BooleanExpression lastIdxLt(Long nextIdx) {
        return nextIdx == null ? null : matchingPost.pk.lt(nextIdx);
    }
    private BooleanExpression lastViewCntLoe(Long viewCnt) {return viewCnt == null ? null : matchingPost.viewCnt.loe(viewCnt);}
    private BooleanExpression lastExpiredDateLoe(LocalDate lastExpiredDate) {return lastExpiredDate == null ? null : matchingPost.expiryDate.loe(lastExpiredDate).and(matchingPost.expiryDate.before(LocalDate.now()));}

    private BooleanExpression recruitmentTypeEq(RecruitmentType recruitmentType) {
        return recruitmentType == null ? null : matchingPost.recruitmentType.eq(recruitmentType);
    }

    private BooleanExpression abilityEq(Ability ability) {
        return ability == null ? null : matchingPost.ability.eq(ability);
    }

    private BooleanExpression genderEq(Gender gender) {
        return gender == null ? null : matchingPost.gender.eq(gender);
    }
    private BooleanExpression recruiterTypeEq(RecruiterType recruiterType) {
        return recruiterType == null ? null : matchingPost.recruiterType.eq(recruiterType);
    }
    private BooleanExpression areaListIn(List<AreaName> areaList) {
        return areaList == null ? null : matchingPost.areaList.any().areaName.in(areaList);
    }

    private BooleanExpression currentDateAfter(LocalDate currentDate) {
        return currentDate == null ? null : matchingPost.expiryDate.after(currentDate);
    }

    private BooleanExpression sportsTypeEq(SportsType sportsType) {
        return matchingPost.sportsCategory.eq(sportsType);
    }

    private BooleanExpression dateFilterEq(LocalDate dateFilter, YearMonth monthFilter) {
        if (dateFilter != null && monthFilter != null) {
            throw new RuntimeException(ExceptionMessage.DATE_MONTH_CANT_COEXIST.getExceptionMessage());
        }
        if (dateFilter == null && monthFilter == null) {
            return null;
        }
        return dateFilter == null ? matchingPost.matchingDates.any().matchingDate.yearMonth().eq(monthFilter.getYear()*100+monthFilter.getMonthValue()) : matchingPost.matchingDates.any().matchingDate.eq(dateFilter);
    }

    private BooleanExpression dateFilterIn(List<LocalDate> dateFilter, YearMonth monthFilter) {

        if (dateFilter != null && monthFilter != null) {
            throw new RuntimeException(ExceptionMessage.DATE_MONTH_CANT_COEXIST.getExceptionMessage());
        }
        if (dateFilter == null && monthFilter == null) {
            return null;
        }
        return dateFilter == null ? matchingPost.matchingDates.any().matchingDate.yearMonth().eq(monthFilter.getYear()*100+monthFilter.getMonthValue()) : matchingPost.matchingDates.any().matchingDate.in(dateFilter);
    }

    private BooleanExpression locationIn(double topLat, double bottomLat, double leftLon, double rightLon) {
        return matchingPost.lat.between(bottomLat, topLat).and(matchingPost.lon.between(leftLon, rightLon));
    }

    private BooleanExpression isExcludeRegionUnit(boolean excludeRegionUnit) {
        return excludeRegionUnit ? matchingPost.locationSelectionType.eq(LocationSelectionType.SEARCH_BASED) : null;
    }

    private OrderSpecifier[] getSortOption(SortOption sortOption) {
        return switch (sortOption) {
            case NEW -> new OrderSpecifier[]{
                    new OrderSpecifier<>(Order.DESC, matchingPost.createdTime),
                    new OrderSpecifier<>(Order.DESC,matchingPost.pk),
            };
            case DEADLINE -> new OrderSpecifier[]{
                    new OrderSpecifier<>(Order.DESC, matchingPost.expiryDate),
                    new OrderSpecifier<>(Order.DESC,matchingPost.pk)
            };
        };
    }

    @Override
    public List<MatchingPost> findCompletedMatchingPosts(Long nextIdx, RecruiterType recruiterType, boolean excludeCompleteMatchesFilter, Member member, List<MatchingPost> matchingPostWithReview) {
        return jpaQueryFactory.selectFrom(matchingPost)
                .where(
                        isTeamMemberInTeam(member),
                        nextIdx(nextIdx),
                        recruiterTypeEq(recruiterType),
                        expiredMatchesFilter(excludeCompleteMatchesFilter, matchingPostWithReview)
                )
                .limit(30)
                .orderBy(matchingPost.createdTime.desc())
                .fetch();
    }

    private BooleanExpression nextIdx(Long nextIdx) {
        return nextIdx == null ? null : matchingPost.pk.loe(nextIdx);
    }

    @Override
    public List<MatchingPost> findMatchingPostInMap(double topLat, double bottomLat, double leftLon, double rightLon) {

        return jpaQueryFactory.selectFrom(matchingPost)
                .where(matchingPost.lat.between(bottomLat,topLat).and(matchingPost.lon.between(leftLon,rightLon))
                )
                .fetch();
    }

    private BooleanExpression isTeamMemberInTeam(Member member){
        return matchingPost.team.teamMembers.any().member.in(member);
    }

    private BooleanExpression expiredMatchesFilter(boolean excludeExpiredMatchesFilter, List<MatchingPost> matchingPostsWithReview){
        return excludeExpiredMatchesFilter
                ? isNotExpiredCompleteMatches(matchingPostsWithReview)
                : allCompleteMatches();
    }

    private BooleanExpression isNotExpiredCompleteMatches(List<MatchingPost> matchingPostsWithReview) {
        return matchingPostsWithReview.isEmpty()
                ? (matchingPost.matchingDates.any().matchingDate.max().after(LocalDate.now()).and(matchingPost.matchingStatus.ne(MatchingStatus.PENDING)))
                    .or(matchingPost.matchingDates.any().matchingDate.min().before(LocalDate.now()).and(matchingPost.matchingStatus.eq(MatchingStatus.COMPLETE)))
                : matchingPost.notIn(matchingPostsWithReview)
                    .and(
                            ((matchingPost.matchingStatus.ne(MatchingStatus.PENDING)).and(matchingPost.matchingDates.any().matchingDate.max().after(LocalDate.now())))
                            .or(matchingPost.matchingStatus.eq(MatchingStatus.COMPLETE).and(matchingPost.matchingDates.any().matchingDate.min().before(LocalDate.now())))
                    );
    }

    private BooleanExpression allCompleteMatches(){
        return matchingPost.matchingStatus.ne(MatchingStatus.PENDING);
    }
}