package com.wemingle.core.domain.team.service;

import com.wemingle.core.domain.category.sports.entity.sportstype.SportsType;
import com.wemingle.core.domain.team.dto.CreateTeamDto;
import com.wemingle.core.domain.team.dto.TeamDto;
import com.wemingle.core.domain.team.entity.Team;

import java.util.HashMap;

public interface TeamService {
    HashMap<Long, TeamDto.ResponseWritableTeamInfoDto> getTeamInfoWithAvailableWrite(SportsType sportsType, String memberId);
    TeamDto.ResponseTeamHomeConditions getTeamHomeConditions(String memberId);
    HashMap<Long, TeamDto.ResponseRecommendationTeamInfo> getRecommendTeams(Long nextIdx, String memberId);
    HashMap<Long, TeamDto.ResponseRecommendationTeamForMemberInfo> getRecommendTeamsForMember(Long nextIdx, String memberId);
    HashMap<Long, TeamDto.ResponseTeamInfoInSearch> getTeamByName(Long nextIdx, String teamName);
    HashMap<Long, TeamDto.ResponseTeamByMemberUniv> getTeamWithMemberUniv(Long nextIdx, String memberId);
    TeamDto.TeamInfo getTeamInfoWithTeam(Long teamPk, String memberId);
    void saveTeam(String ownerId, CreateTeamDto createTeamDto);
    TeamDto.ResponseTeamParticipantCond getTeamParticipantCond(Long teamPk, String memberId);
    HashMap<Long, TeamDto.ResponseWritableTeamInfoDto> getRequestableTeamsInfo(Long matchingPostPk, SportsType sportsType, String memberId);
    Team findByTeamPk(Long teamPk);
    TeamDto.ResponseTeamSetting getTeamSetting(Long teamPk);
    void updateTeamSetting(TeamDto.RequestTeamSettingUpdate updateDto);
}
