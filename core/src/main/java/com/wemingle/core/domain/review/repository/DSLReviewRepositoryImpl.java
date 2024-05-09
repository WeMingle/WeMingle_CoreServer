package com.wemingle.core.domain.review.repository;

import com.querydsl.core.QueryFactory;
import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.jpa.impl.JPAQueryFactory;
import com.wemingle.core.domain.review.entity.QTeamReview;
import com.wemingle.core.domain.review.entity.TeamReview;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.util.List;

import static com.wemingle.core.domain.review.entity.QTeamReview.teamReview;

@Repository
@RequiredArgsConstructor
public class DSLReviewRepositoryImpl implements DSLReviewRepository {
    private final JPAQueryFactory jpaQueryFactory;

    @Override
    public List<TeamReview> findMyReviews(String memberId, Long nextIdx) {
        return jpaQueryFactory.selectFrom(teamReview)
                .where(
                        teamReview.reviewer.member.memberId.eq(memberId),
                        nextIdxLoe(nextIdx)
                )
                .limit(30)
                .orderBy(teamReview.pk.desc())
                .fetch();
    }

    @Override
    public List<TeamReview> findGroupReviews(Long groupId, Long nextIdx) {
        return jpaQueryFactory.selectFrom(teamReview)
                .where(
                        teamReview.reviewee.pk.eq(groupId),
                        nextIdxLoe(nextIdx)
                )
                .limit(30)
                .orderBy(teamReview.pk.desc())
                .fetch();
    }

    BooleanExpression nextIdxLoe(Long nextIdx) {
        return nextIdx == null ? null : teamReview.pk.loe(nextIdx);
    }
}
