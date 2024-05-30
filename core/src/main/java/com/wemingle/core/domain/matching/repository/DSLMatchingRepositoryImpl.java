package com.wemingle.core.domain.matching.repository;

import com.querydsl.jpa.JPAExpressions;
import com.querydsl.jpa.JPQLQuery;
import com.querydsl.jpa.impl.JPAQueryFactory;
import com.wemingle.core.domain.matching.entity.QMatching;
import com.wemingle.core.domain.post.entity.MatchingPostMatchingDate;
import com.wemingle.core.domain.post.entity.QMatchingPostMatchingDate;
import com.wemingle.core.domain.post.entity.matchingstatus.MatchingStatus;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;

import static com.wemingle.core.domain.matching.entity.QMatching.matching;
import static com.wemingle.core.domain.post.entity.QMatchingPostMatchingDate.matchingPostMatchingDate;

@Slf4j
@Repository
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class DSLMatchingRepositoryImpl implements DSLMatchingRepository{

    private final JPAQueryFactory jpaQueryFactory;
    @Override
    public Integer findCompleteMatchingCnt(String memberId, MatchingStatus matchingStatus) {

        JPQLQuery<Long> subQuery = JPAExpressions.select(matchingPostMatchingDate.matchingPost.pk)
                .from(matchingPostMatchingDate)
                .groupBy(matchingPostMatchingDate.matchingPost.pk)
                .having(matchingPostMatchingDate.matchingDate.max().lt(LocalDate.now()));

        Long cnt = jpaQueryFactory.select(matching.count())
                .from(matching)
                .where(
                        matching.member.memberId.eq(memberId),
                        matching.matchingPost.matchingStatus.eq(matchingStatus),
                        matching.matchingPost.pk.in(subQuery)
                ).fetchOne();
        return cnt == null ? 0 : cnt.intValue();
    }

    @Override
    public Integer findScheduledMatchingCnt(String memberId, MatchingStatus matchingStatus) {
        JPQLQuery<Long> subQuery = JPAExpressions.select(matchingPostMatchingDate.matchingPost.pk)
                .from(matchingPostMatchingDate)
                .groupBy(matchingPostMatchingDate.matchingPost.pk)
                .having(matchingPostMatchingDate.matchingDate.min().lt(LocalDate.now()));

        Long cnt = jpaQueryFactory.select(matching.count())
                .from(matching)
                .where(
                        matching.member.memberId.eq(memberId),
                        matching.matchingPost.matchingStatus.eq(matchingStatus),
                        matching.matchingPost.pk.in(subQuery)
                ).fetchOne();
        return cnt == null ? 0 : cnt.intValue();
    }
}
