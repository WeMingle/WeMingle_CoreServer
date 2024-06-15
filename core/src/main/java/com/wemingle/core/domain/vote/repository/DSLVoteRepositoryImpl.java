package com.wemingle.core.domain.vote.repository;

import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.jpa.impl.JPAQueryFactory;
import com.wemingle.core.domain.post.entity.TeamPost;
import com.wemingle.core.domain.vote.entity.TeamPostVote;
import com.wemingle.core.domain.vote.entity.votestatus.VoteStatus;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

import static com.wemingle.core.domain.vote.entity.QTeamPostVote.teamPostVote;

@Repository
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class DSLVoteRepositoryImpl implements DSLVoteRepository {
    private final JPAQueryFactory jpaQueryFactory;

    private static final int PAGE_SIZE = 50;


    @Override
    public List<TeamPostVote> getExpiredVotes(Long nextIdx, List<TeamPost> teamPosts) {
        if (teamPosts.isEmpty()){
            return null;
        }

        return jpaQueryFactory.selectFrom(teamPostVote)
                .where(
                        nextIdxLoe(nextIdx),
                        teamPostVote.teamPost.in(teamPosts),
                        expiredVoteFilter(),
                        realNameVoteFilter()
                )
                .limit(PAGE_SIZE)
                .orderBy(teamPostVote.pk.desc())
                .fetch();
    }

    private BooleanExpression nextIdxLoe(Long nextIdx){
        return nextIdx == null ? null : teamPostVote.pk.loe(nextIdx);
    }
    private BooleanExpression expiredVoteFilter() {
        return teamPostVote.expiryTime.lt(LocalDateTime.now()).or(teamPostVote.voteStatus.eq(VoteStatus.COMPLETE));
    }
    private BooleanExpression realNameVoteFilter() {
        return teamPostVote.isAnonymousVoting.eq(false);
    }
}
