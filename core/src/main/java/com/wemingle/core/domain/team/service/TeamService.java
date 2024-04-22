package com.wemingle.core.domain.team.service;

import com.wemingle.core.domain.team.dto.CreateTeamDto;
import com.wemingle.core.domain.team.dto.TeamDto;

import java.util.HashMap;

public interface TeamService {
    HashMap<Long, TeamDto.ResponseTeamInfoDto> getTeamInfoWithMemberId(String memberId);
    TeamDto.ResponseTeamHomeConditions getTeamHomeConditions(String memberId);
    HashMap<Long, TeamDto.ResponseRecommendationTeamInfo> getRecommendTeams(Long nextIdx, String memberId);
    HashMap<Long, TeamDto.ResponseRecommendationTeamForMemberInfo> getRecommendTeamsForMember(Long nextIdx, String memberId);
    TeamDto.ResponseTeamInfoByName getTeamByName(Long nextIdx, String teamName);
    HashMap<Long, TeamDto.ResponseTeamByMemberUniv> getTeamWithMemberUniv(Long nextIdx, String memberId);
    TeamDto.TeamInfo getTeamInfoWithTeam(Long teamPk);
    void saveTeam(String ownerId, CreateTeamDto createTeamDto);
}
