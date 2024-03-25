package com.wemingle.core.domain.team.service;

import com.wemingle.core.domain.img.service.S3ImgService;
import com.wemingle.core.domain.team.dto.TeamDto;
import com.wemingle.core.domain.team.entity.Team;
import com.wemingle.core.domain.team.repository.TeamRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashMap;
import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class TeamServiceImpl implements TeamService{
    private final TeamRepository teamRepository;
    private final S3ImgService s3ImgService;
    @Override
    public HashMap<Long, TeamDto.ResponseTeamInfoDto> getTeamInfoWithMemberId(String memberId) {
        List<Team> teamList = teamRepository.findByTeamOwner_MemberId(memberId);

        HashMap<Long, TeamDto.ResponseTeamInfoDto> responseTeamInfo = new HashMap<>();

        teamList.forEach(team -> responseTeamInfo.put(team.getPk(),
                        TeamDto.ResponseTeamInfoDto.builder()
                                .teamName(team.getTeamName())
                                .teamImgUrl(s3ImgService.getGroupProfilePicUrl(team.getProfileImgId()))
                                .build()));

        return responseTeamInfo;
    }
}
