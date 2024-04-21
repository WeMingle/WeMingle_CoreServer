package com.wemingle.core.domain.post.repository;

import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.jpa.impl.JPAQueryFactory;
import com.wemingle.core.domain.post.entity.TeamPost;
import com.wemingle.core.domain.team.entity.Team;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Repository;

import java.util.List;

import static com.wemingle.core.domain.post.entity.QTeamPost.teamPost;
import static com.wemingle.core.domain.team.entity.QTeam.team;

@Repository
@RequiredArgsConstructor
public class DSLTeamPostRepositoryImpl implements DSLTeamPostRepository{
    private final JPAQueryFactory jpaQueryFactory;

    @Override
    public List<TeamPost> getTeamPostWithMember(Long nextIdx, List<Team> myTeams, Pageable pageable) {
        return jpaQueryFactory.selectFrom(teamPost)
                .where(
                        nextIdxLt(nextIdx),
                        teamPost.team.in(myTeams)
                )
                .limit(pageable.getPageSize())
                .orderBy(teamPost.pk.desc())
                .fetch();
    }

    private BooleanExpression nextIdxLt(Long nextIdx) {
        return nextIdx == null ? null : team.pk.loe(nextIdx);
    }
}
