package com.wemingle.core.domain.team.service;

import com.wemingle.core.domain.img.service.S3ImgService;
import com.wemingle.core.domain.team.dto.TeamDto;
import com.wemingle.core.domain.team.entity.TeamMember;
import com.wemingle.core.domain.team.repository.TeamMemberRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.List;

@RequiredArgsConstructor
@Service
public class TeamMemberService {
    private final TeamMemberRepository teamMemberRepository;
    private final S3ImgService s3ImgService;
    public HashMap<Long, TeamDto.ResponseTeamInfoDto> getTeamsAsLeaderOrMember(String memberId) {
        List<TeamMember> teamsAsLeaderOrMember = teamMemberRepository.findTeamsAsLeaderOrMember(memberId);
        HashMap<Long, TeamDto.ResponseTeamInfoDto> responseTeamInfo = new HashMap<>();
        teamsAsLeaderOrMember.forEach(teamMember -> responseTeamInfo.put(teamMember.getPk(),
                TeamDto.ResponseTeamInfoDto.builder()
                        .teamName(teamMember.getTeam().getTeamName())
                        .teamImgUrl(s3ImgService.getGroupProfilePicUrl(teamMember.getProfileImg()))
                        .build()));
        return responseTeamInfo;
    }
}
