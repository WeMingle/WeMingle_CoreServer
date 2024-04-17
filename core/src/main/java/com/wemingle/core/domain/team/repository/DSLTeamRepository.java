package com.wemingle.core.domain.team.repository;

import com.wemingle.core.domain.member.entity.Member;
import com.wemingle.core.domain.team.entity.Team;
import org.springframework.data.domain.Pageable;

import java.util.List;

public interface DSLTeamRepository {
    List<Team> getTeamByTeamName(Long nextIdx,
                                 String teamName,
                                 Pageable pageable);
    List<Team> getRecommendationTeams(Long nextIdx,
                                      List<Team> excludeTeams,
                                      Long remainNum,
                                      Pageable pageable);

    List<Team> getTeamsByTeamOwners(Long nextIdx,
                                 List<Member> members,
                                 List<Team> myTeams,
                                 Pageable pageable);
}
