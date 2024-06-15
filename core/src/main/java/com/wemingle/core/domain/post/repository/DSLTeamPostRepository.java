package com.wemingle.core.domain.post.repository;

import com.wemingle.core.domain.post.dto.searchoption.SearchOption;
import com.wemingle.core.domain.post.entity.TeamPost;
import com.wemingle.core.domain.team.entity.Team;

import java.util.List;

public interface DSLTeamPostRepository {
    List<TeamPost> getTeamPostWithMember(Long nextIdx, List<Team> myTeams);
    List<TeamPost> getTeamPostWithTeam(Long nextIdx, Team team, boolean isNotice);
    List<TeamPost> getSearchTeamPost(Long nextIdx, Team team, String query, SearchOption searchOption);
    List<TeamPost> findMyTeamPosts(Long nextIdx, Long teamId, String memberId);
}
