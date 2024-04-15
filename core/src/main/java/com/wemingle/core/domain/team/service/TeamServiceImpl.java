package com.wemingle.core.domain.team.service;

import com.wemingle.core.domain.img.service.S3ImgService;
import com.wemingle.core.domain.member.entity.Member;
import com.wemingle.core.domain.member.repository.MemberRepository;
import com.wemingle.core.domain.team.dto.TeamDto;
import com.wemingle.core.domain.team.entity.Team;
import com.wemingle.core.domain.team.repository.TeamMemberRepository;
import com.wemingle.core.domain.team.repository.TeamRepository;
import com.wemingle.core.global.exceptionmessage.ExceptionMessage;
import jakarta.persistence.EntityNotFoundException;
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
    private final MemberRepository memberRepository;
    private final TeamMemberRepository teamMemberRepository;
    @Override
    public HashMap<Long, TeamDto.ResponseTeamInfoDto> getTeamInfoWithMemberId(String memberId) {
        List<Team> teamList = teamRepository.findByTeamOwner_MemberId(memberId);

        HashMap<Long, TeamDto.ResponseTeamInfoDto> responseTeamInfo = new HashMap<>();

        teamList.forEach(team -> responseTeamInfo.put(team.getPk(),
                        TeamDto.ResponseTeamInfoDto.builder()
                                .teamName(team.getTeamName())
                                .teamImgUrl(getTeamImgUrl(memberId, team))
                                .build()));

        return responseTeamInfo;
    }

    private String getTeamImgUrl(String memberId, Team team) {
        return team.getTeamName().equals(memberId) 
                ? s3ImgService.getMemberProfilePicUrl(team.getProfileImgId())
                : s3ImgService.getGroupProfilePicUrl(team.getProfileImgId());
    }

    @Override
    public boolean isPresentTeamWithMe(String memberId) {
        Member member = memberRepository.findByMemberId(memberId)
                .orElseThrow(() -> new EntityNotFoundException(ExceptionMessage.MEMBER_NOT_FOUNT.getExceptionMessage()));

        return teamMemberRepository.existsByMember(member);
    }
}
