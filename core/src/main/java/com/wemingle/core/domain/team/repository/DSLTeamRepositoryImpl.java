package com.wemingle.core.domain.team.repository;

import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.jpa.impl.JPAQueryFactory;
import com.wemingle.core.domain.team.entity.Team;
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
                        team.teamName.contains(teamName)
                )
                .offset(pageable.getOffset())
                .limit(pageable.getPageSize())
                .orderBy(team.pk.desc())
                .fetch();
    }

    private BooleanExpression nextIdxLt(Long nextIdx) {
        return nextIdx == null ? null : team.pk.loe(nextIdx);
    }
}
