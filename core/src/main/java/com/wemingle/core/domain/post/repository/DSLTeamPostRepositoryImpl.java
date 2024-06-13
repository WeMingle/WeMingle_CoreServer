package com.wemingle.core.domain.post.repository;

import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.jpa.impl.JPAQueryFactory;
import com.wemingle.core.domain.post.dto.searchoption.SearchOption;
import com.wemingle.core.domain.post.entity.TeamPost;
import com.wemingle.core.domain.post.entity.posttype.PostType;
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
        if (myTeams.isEmpty())
            return null;

        return jpaQueryFactory.selectFrom(teamPost)
                .where(
                        nextIdxLoe(nextIdx),
                        teamPost.team.in(myTeams)
                )
                .limit(PAGE_SIZE)
                .orderBy(teamPost.pk.desc())
                .fetch();
    }

    @Override
    public List<TeamPost> getTeamPostWithTeam(Long nextIdx, Team team, boolean isNotice) {
        return jpaQueryFactory.selectFrom(teamPost)
                .where(
                        nextIdxLoe(nextIdx),
                        teamPost.team.eq(team),
                        isNotice(isNotice)
                )
                .limit(PAGE_SIZE)
                .orderBy(teamPost.pk.desc())
                .fetch();
    }

    private BooleanExpression isNotice(boolean isNotice) {
        return isNotice ? teamPost.postType.eq(PostType.NOTICE) : null;
    }

    private BooleanExpression nextIdxLoe(Long nextIdx) {
        return nextIdx == null ? null : teamPost.pk.loe(nextIdx);
    }

    @Override
    public List<TeamPost> getSearchTeamPost(Long nextIdx, Team team, String searchWord, SearchOption searchOption) {
        return jpaQueryFactory.selectFrom(teamPost)
                .where(
                        nextIdxLoe(nextIdx),
                        teamPost.team.eq(team),
                        searchCond(searchOption, searchWord)
                )
                .limit(30)
                .orderBy(teamPost.pk.desc())
                .fetch();
    }

    private BooleanExpression searchCond(SearchOption searchOption, String searchWord){
        return switch (searchOption) {
            case TITLE -> teamPost.title.contains(searchWord);
            case WRITER -> teamPost.writer.nickname.contains(searchWord);
        };
    }

    @Override
    public List<TeamPost> findMyTeamPosts(Long nextIdx, Long teamId, String memberId) {
        return jpaQueryFactory.selectFrom(teamPost)
                .where(
                        teamPost.pk.eq(teamId),
                        teamPost.pk.lt(nextIdx),
                        teamPost.writer.member.memberId.eq(memberId)
                ).limit(30)
                .orderBy(teamPost.pk.desc())
                .fetch();
    }
}
