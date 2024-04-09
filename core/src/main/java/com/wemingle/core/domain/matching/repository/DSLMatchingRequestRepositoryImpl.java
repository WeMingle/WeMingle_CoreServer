package com.wemingle.core.domain.matching.repository;

import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.jpa.impl.JPAQueryFactory;
import com.wemingle.core.domain.matching.controller.requesttype.RequestType;
import com.wemingle.core.domain.matching.entity.MatchingRequest;
import com.wemingle.core.domain.member.entity.Member;
import com.wemingle.core.domain.post.entity.MatchingPost;
import com.wemingle.core.domain.post.entity.matchingstatus.MatchingStatus;
import com.wemingle.core.domain.post.entity.recruitertype.RecruiterType;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

import static com.wemingle.core.domain.matching.entity.QMatching.matching;
import static com.wemingle.core.domain.matching.entity.QMatchingRequest.matchingRequest;
import static com.wemingle.core.global.exceptionmessage.ExceptionMessage.INVALID_REQUEST_TYPE;

@Slf4j
@Repository
@RequiredArgsConstructor
@Transactional(readOnly = true)
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

    @Override
    public List<MatchingRequest> findMatchingRequestHistories(Long nextIdx,
                                                              RequestType requestType,
                                                              RecruiterType recruiterType,
                                                              boolean excludeCompleteMatchesFilter,
                                                              Member member,
                                                              List<MatchingPost> myMatchingPosts,
                                                              Pageable pageable) {
      return jpaQueryFactory.selectFrom(matchingRequest)
              .where(
                      nextIdxLt(nextIdx),
                      recruiterTypeEq(recruiterType),
                      requestTypeEq(requestType, myMatchingPosts, member),
                      excludeCompleteMatchesFilter(excludeCompleteMatchesFilter)
              )
              .offset(pageable.getOffset())
              .limit(pageable.getPageSize())
              .orderBy(matchingRequest.pk.desc())
              .fetch();
    }

    private BooleanExpression nextIdxLt(Long nextIdx) {
        return nextIdx == null ? null : matchingRequest.pk.loe(nextIdx);
    }

    private BooleanExpression recruiterTypeEq(RecruiterType recruiterType) {
        return recruiterType == null ? null : matchingRequest.matchingPost.recruiterType.eq(recruiterType);
    }

    private BooleanExpression requestTypeEq(RequestType requestType, List<MatchingPost> myMatchingPosts, Member member){
        if (requestType == null) {
            return sentRequest(member).or(receiveRequest(myMatchingPosts));
        }

        switch (requestType){
            case SENT -> {
                return sentRequest(member);
            }
            case RECEIVED -> {
                return receiveRequest(myMatchingPosts);
            }
            default -> throw new RuntimeException(INVALID_REQUEST_TYPE.getExceptionMessage());
        }
    }

    private BooleanExpression sentRequest(Member member){
        return matchingRequest.member.eq(member);
    }

    private BooleanExpression receiveRequest(List<MatchingPost> myMatchingPosts){
        if (myMatchingPosts.isEmpty()){
            return null;
        }

        return matchingRequest.matchingPost.in(myMatchingPosts)
                .and(teamOwnerFilter());
    }

    private BooleanExpression excludeCompleteMatchesFilter(boolean excludeCompleteMatchesFilter){
        return excludeCompleteMatchesFilter
                ? matchingRequest.matchingRequestStatus.ne(MatchingStatus.COMPLETE)
                : null;
    }

    private BooleanExpression teamOwnerFilter(){
        return matchingRequest.member.eq(matchingRequest.team.teamOwner);
    }
}
