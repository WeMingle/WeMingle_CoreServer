package com.wemingle.core.domain.team.service;

import com.wemingle.core.domain.img.service.S3ImgService;
import com.wemingle.core.domain.member.dto.TeamMemberDto;
import com.wemingle.core.domain.member.repository.MemberRepository;
import com.wemingle.core.domain.team.dto.TeamDto;
import com.wemingle.core.domain.team.entity.TeamMember;
import com.wemingle.core.domain.team.repository.TeamMemberRepository;
import com.wemingle.core.domain.team.repository.TeamRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;

@RequiredArgsConstructor
@Service
public class TeamMemberService {
    private final TeamRepository teamRepository;
    private final TeamMemberRepository teamMemberRepository;
    private final S3ImgService s3ImgService;
    private final MemberRepository memberRepository;
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

    public HashMap<Long, TeamMemberDto.ResponseTeamMembers> getTeamMembersInTeam(Long teamPk, String memberId){
        List<TeamMember> teamMembersWithoutMe = teamMemberRepository.findWithTeamWithoutMe(teamPk, memberId);

        LinkedHashMap<Long, TeamMemberDto.ResponseTeamMembers> responseData = new LinkedHashMap<>();

        teamMembersWithoutMe.forEach(teamMember -> responseData.put(teamMember.getPk(), TeamMemberDto.ResponseTeamMembers.builder()
                .nickname(teamMember.getNickname())
                .imgUrl(s3ImgService.getTeamMemberPreSignedUrl(teamMember.getProfileImg()))
                .build()
        ));

        return responseData;
    }

    public List<String> getTeamMembersImgUrl(List<Long> teamMembersPk){
        List<TeamMember> teamMembers = teamMemberRepository.findAllById(teamMembersPk);

        return teamMembers.stream()
                .map(teamMember -> s3ImgService.getTeamMemberPreSignedUrl(teamMember.getProfileImg()))
                .toList();
    }
}
