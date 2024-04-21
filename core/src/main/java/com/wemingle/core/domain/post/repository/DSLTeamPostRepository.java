package com.wemingle.core.domain.post.repository;

import com.wemingle.core.domain.post.entity.TeamPost;
import com.wemingle.core.domain.team.entity.Team;
import org.springframework.data.domain.Pageable;

import java.util.List;

public interface DSLTeamPostRepository {
    List<TeamPost> getTeamPostWithMember(Long nextIdx, List<Team> myTeams, Pageable pageable);
}
