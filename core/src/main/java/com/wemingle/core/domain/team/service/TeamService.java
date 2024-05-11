package com.wemingle.core.domain.team.service;

import com.wemingle.core.domain.team.dto.CreateTeamDto;
import com.wemingle.core.domain.team.dto.TeamDto;

import java.util.HashMap;

public interface TeamService {
    HashMap<Long, TeamDto.ResponseWritableTeamInfoDto> getTeamInfoWithAvailableWrite(String memberId);
    TeamDto.ResponseTeamHomeConditions getTeamHomeConditions(String memberId);
    HashMap<Long, TeamDto.ResponseRecommendationTeamInfo> getRecommendTeams(Long nextIdx, String memberId);
    HashMap<Long, TeamDto.ResponseRecommendationTeamForMemberInfo> getRecommendTeamsForMember(Long nextIdx, String memberId);
    HashMap<Long, TeamDto.ResponseTeamInfoInSearch> getTeamByName(Long nextIdx, String teamName);
    HashMap<Long, TeamDto.ResponseTeamByMemberUniv> getTeamWithMemberUniv(Long nextIdx, String memberId);
    TeamDto.TeamInfo getTeamInfoWithTeam(Long teamPk);
    void saveTeam(String ownerId, CreateTeamDto createTeamDto);
    TeamDto.ResponseTeamParticipantCond getTeamParticipantCond(Long teamPk, String memberId);
    HashMap<Long, TeamDto.ResponseWritableTeamInfoDto> getRequestableTeamsInfo(Long matchingPostPk, String memberId);
}
