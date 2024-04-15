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
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Optional;

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

    @Override
    public HashMap<Long, TeamDto.ResponseRandomTeamInfo> getRandomTeam() {
        List<Team> randomTeams = teamRepository.getRandomTeam();
        HashMap<Long, TeamDto.ResponseRandomTeamInfo> responseHashMap = new HashMap<>();

         randomTeams.forEach(randomTeam -> responseHashMap.put(randomTeam.getPk(), TeamDto.ResponseRandomTeamInfo.builder()
                 .teamName(randomTeam.getTeamName())
                 .content(randomTeam.getContent())
                 .recruitmentType(randomTeam.getRecruitmentType())
                 .teamImgUrl(s3ImgService.getGroupProfilePicUrl(randomTeam.getProfileImgId()))
                 .build()));

         return responseHashMap;
    }

    @Override
    public TeamDto.ResponseTeamInfoByName getTeamByName(Long nextIdx, String teamName) {
        PageRequest pageRequest = PageRequest.of(0, 4);
        List<Team> teams = teamRepository.getTeamByTeamName(nextIdx, teamName, pageRequest);

        LinkedHashMap<Long, TeamDto.TeamInfoInSearch> teamInfoHashMap = new LinkedHashMap<>();
        teams.forEach(team -> teamInfoHashMap.put(team.getPk(), TeamDto.TeamInfoInSearch.builder()
                .teamName(team.getTeamName())
                .content(team.getContent())
                .recruitmentType(team.getRecruitmentType())
                .teamImgUrl(s3ImgService.getGroupProfilePicUrl(team.getProfileImgId()))
                .build()
        ));

        boolean hasNextTeam = isExistedNextTeam(teams, teamName);

        return TeamDto.ResponseTeamInfoByName.builder()
                .teamsInfo(teamInfoHashMap)
                .hasNextTeam(hasNextTeam)
                .build();
    }

    private boolean isExistedNextTeam(List<Team> teams, String teamName) {
        Optional<Long> minPk = teams.stream().map(Team::getPk).min(Long::compareTo);
        boolean hasNextData = false;
        if (minPk.isPresent()) {
            hasNextData = teamRepository.existsByPkLessThanAndTeamNameContains(minPk.get(), teamName);
        }

        return hasNextData;
    }
}
