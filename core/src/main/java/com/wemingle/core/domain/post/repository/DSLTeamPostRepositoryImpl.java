package com.wemingle.core.domain.post.repository;

import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.jpa.impl.JPAQueryFactory;
import com.wemingle.core.domain.post.entity.TeamPost;
import com.wemingle.core.domain.team.entity.Team;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.util.List;

import static com.wemingle.core.domain.post.entity.QTeamPost.teamPost;

@Repository
@RequiredArgsConstructor
public class DSLTeamPostRepositoryImpl implements DSLTeamPostRepository{
    private final JPAQueryFactory jpaQueryFactory;
    private final static long PAGE_SIZE = 30;

    @Override
    public List<TeamPost> getTeamPostWithMember(Long nextIdx, List<Team> myTeams) {
        return jpaQueryFactory.selectFrom(teamPost)
                .where(
                        nextIdxLt(nextIdx),
                        teamPost.team.in(myTeams)
                )
                .limit(PAGE_SIZE)
                .orderBy(teamPost.pk.desc())
                .fetch();
    }

    @Override
    public List<TeamPost> getTeamPostWithTeam(Long nextIdx, Team team) {
        return jpaQueryFactory.selectFrom(teamPost)
                .where(
                        nextIdxLt(nextIdx),
                        teamPost.team.eq(team)
                )
                .limit(PAGE_SIZE)
                .orderBy(teamPost.pk.desc())
                .fetch();
    }

    private BooleanExpression nextIdxLt(Long nextIdx) {
        return nextIdx == null ? null : teamPost.pk.loe(nextIdx);
    }
}
