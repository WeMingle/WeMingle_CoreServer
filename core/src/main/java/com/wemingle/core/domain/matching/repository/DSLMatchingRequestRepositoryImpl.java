package com.wemingle.core.domain.matching.repository;

import com.querydsl.jpa.impl.JPAQueryFactory;
import com.wemingle.core.domain.post.entity.MatchingPost;
import com.wemingle.core.domain.post.entity.matchingstatus.MatchingStatus;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.util.List;

import static com.wemingle.core.domain.matching.entity.QMatching.matching;
import static com.wemingle.core.domain.matching.entity.QMatchingRequest.matchingRequest;

@Repository
@RequiredArgsConstructor
public class DSLMatchingRequestRepositoryImpl implements DSLMatchingRequestRepository{
    private final JPAQueryFactory jpaQueryFactory;
    @Override
    public Integer findReceivedMatchingCnt(String memberId) {

        List<MatchingPost> matchingPostList = jpaQueryFactory.select(matching.matchingPost)
                .from(matching)
                .where(
                        matching.member.memberId.eq(memberId),
                        matching.matchingPost.team.teamMembers.any().member.memberId.eq(memberId),
                        matching.matchingPost.matchingStatus.eq(MatchingStatus.PENDING)
                )
                .fetch();

        Long cnt = jpaQueryFactory.select(matchingRequest.count())
                .from(matchingRequest)
                .where(
                        matchingRequest.matchingPost.in(matchingPostList),
                        matchingRequest.matchingRequestStatus.eq(MatchingStatus.PENDING))
                .fetchOne();

        return cnt == null ? 0 : cnt.intValue();
    }
}
