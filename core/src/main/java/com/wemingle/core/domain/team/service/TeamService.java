package com.wemingle.core.domain.team.service;

import com.wemingle.core.domain.team.dto.TeamDto;

import java.util.HashMap;

public interface TeamService {
    HashMap<Long, TeamDto.ResponseTeamInfoDto> getTeamInfoWithMemberId(String memberId);
    TeamDto.ResponseTeamHomeConditions getTeamHomeConditions(String memberId);
    HashMap<Long, TeamDto.ResponseRandomTeamInfo> getRecommendTeams(Long nextIdx, String memberId);
    TeamDto.ResponseTeamInfoByName getTeamByName(Long nextIdx, String teamName);
}
