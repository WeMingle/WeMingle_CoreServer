package com.wemingle.core.domain.team.service;

import com.wemingle.core.domain.team.dto.TeamDto;

import java.util.HashMap;

public interface TeamService {
    HashMap<Long, TeamDto.ResponseTeamInfoDto> getTeamInfoWithMemberId(String memberId);
}
