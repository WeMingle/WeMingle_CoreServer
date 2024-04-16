package com.wemingle.core.domain.team.repository;

import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.jpa.impl.JPAQueryFactory;
import com.wemingle.core.domain.team.entity.Team;
import com.wemingle.core.domain.team.entity.teamtype.TeamType;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

import static com.wemingle.core.domain.team.entity.QTeam.team;

@Slf4j
@Repository
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class DSLTeamRepositoryImpl implements DSLTeamRepository{
    private final JPAQueryFactory jpaQueryFactory;
    @Override
    public List<Team> getTeamByTeamName(Long nextIdx,
                                        String teamName,
                                        Pageable pageable) {
        return jpaQueryFactory.selectFrom(team)
                .where(
                        nextIdxLt(nextIdx),
                        team.teamName.contains(teamName),
                        team.teamType.eq(TeamType.TEAM)
                )
                .offset(pageable.getOffset())
                .limit(pageable.getPageSize())
                .orderBy(team.pk.desc())
                .fetch();
    }

    @Override
    public List<Team> getRecommendationTeams(Long nextIdx, List<Team> myTeams, Long remainNum, Pageable pageable) {
        return jpaQueryFactory.selectFrom(team)
                .where(
                        nextIdxLt(nextIdx),
                        notInTeams(myTeams),
                        randomTeamPkEq(remainNum),
                        team.teamType.eq(TeamType.TEAM))
                .offset(pageable.getOffset())
                .limit(pageable.getPageSize())
                .orderBy(team.pk.desc())
                .fetch();
    }

    private BooleanExpression randomTeamPkEq(Long remainNum) {
        return team.pk.mod(2L).eq(remainNum);
    }

    private BooleanExpression nextIdxLt(Long nextIdx) {
        return nextIdx == null ? null : team.pk.loe(nextIdx);
    }
    private BooleanExpression notInTeams(List<Team> myTeams) {
        return myTeams.isEmpty() ? null : team.notIn(myTeams);
    }
}
