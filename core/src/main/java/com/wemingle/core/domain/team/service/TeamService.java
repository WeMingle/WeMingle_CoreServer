package com.wemingle.core.domain.team.service;

import com.wemingle.core.domain.team.dto.TeamDto;

import java.util.HashMap;

public interface TeamService {
    HashMap<Long, TeamDto.ResponseTeamInfoDto> getTeamInfoWithMemberId(String memberId);
    boolean isPresentTeamWithMe(String memberId);
    HashMap<Long, TeamDto.ResponseRandomTeamInfo> getRandomTeam();
    TeamDto.ResponseTeamInfoByName getTeamByName(Long nextIdx, String teamName);
}
