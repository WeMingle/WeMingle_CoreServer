package com.wemingle.core.domain.post.repository;

import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.jpa.impl.JPAQueryFactory;
import com.wemingle.core.domain.post.entity.MatchingPost;
import com.wemingle.core.domain.post.entity.abillity.Ability;
import com.wemingle.core.domain.post.entity.area.AreaName;
import com.wemingle.core.domain.post.entity.gender.Gender;
import com.wemingle.core.domain.post.entity.recruitertype.RecruiterType;
import com.wemingle.core.domain.team.entity.recruitmenttype.RecruitmentType;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;

import static com.wemingle.core.domain.post.entity.QMatchingPost.matchingPost;

@Repository
@RequiredArgsConstructor
public class DSLMatchingPostRepositoryImpl implements DSLMatchingPostRepository{
    private final JPAQueryFactory jpaQueryFactory;

    @Override
    public List<MatchingPost> findFilteredMatchingPost(Long nextIdx,
                                                       RecruitmentType recruitmentType,
                                                       Ability ability,
                                                       Gender gender,
                                                       RecruiterType recruiterType,
                                                       List<AreaName> areaList,
                                                       LocalDate currentDate,
                                                       LocalDate dateFilter,
                                                       Pageable pageable) {
        return jpaQueryFactory.selectFrom(matchingPost)
                .join(matchingPost.team).fetchJoin()
                .join(matchingPost.writer).fetchJoin()
                .join(matchingPost.writer.team).fetchJoin()
                .join(matchingPost.areaList).fetchJoin()
                .where(
                        nextIdxLt(nextIdx),
                        recruitmentTypeEq(recruitmentType),
                        abilityEq(ability),
                        genderEq(gender),
                        recruiterTypeEq(recruiterType),
                        areaListIn(areaList),
                        currentDateAfter(currentDate),
                        dateFilterEq(dateFilter)
                )
                .offset(pageable.getOffset())
                .limit(pageable.getPageSize())
                .orderBy(matchingPost.createdTime.desc())
                .fetch();
    }

    private BooleanExpression nextIdxLt(Long nextIdx) {
        return nextIdx == null ? null : matchingPost.pk.loe(nextIdx);
    }

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

    private BooleanExpression dateFilterEq(LocalDate dateFilter) {
        return dateFilter == null ? null : matchingPost.matchingDate.eq(dateFilter);
    }

    @Override
    public List<MatchingPost> findFilteredMatchingPostInMatchingFeed(Long nextIdx, RecruiterType recruiterType, boolean completeMatchesFilter, Pageable pageable) {
        return jpaQueryFactory.selectFrom(matchingPost)
                .join(matchingPost.team)
//                .where(matchingPost.team)
                .where(nextIdxLt(nextIdx))
                .where(recruiterTypeEq(recruiterType))
                .offset(pageable.getOffset())
                .limit(pageable.getPageSize())
                .orderBy(matchingPost.pk.desc())
                .fetch();
    }
}
