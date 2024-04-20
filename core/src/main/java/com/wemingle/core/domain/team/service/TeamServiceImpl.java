package com.wemingle.core.domain.team.service;

import com.wemingle.core.domain.img.service.S3ImgService;
import com.wemingle.core.domain.member.entity.Member;
import com.wemingle.core.domain.member.repository.MemberRepository;
import com.wemingle.core.domain.memberunivemail.repository.VerifiedUniversityEmailRepository;
import com.wemingle.core.domain.team.dto.TeamDto;
import com.wemingle.core.domain.team.entity.Team;
import com.wemingle.core.domain.team.entity.teamtype.TeamType;
import com.wemingle.core.domain.team.repository.TeamMemberRepository;
import com.wemingle.core.domain.team.repository.TeamRepository;
import com.wemingle.core.domain.univ.entity.UnivEntity;
import com.wemingle.core.global.exceptionmessage.ExceptionMessage;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
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
    private final VerifiedUniversityEmailRepository verifiedUniversityEmailRepository;

    private static final int PAGE_SIZE = 30;
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
    public TeamDto.ResponseTeamHomeConditions getTeamHomeConditions(String memberId) {
        Member member = memberRepository.findByMemberId(memberId)
                .orElseThrow(() -> new EntityNotFoundException(ExceptionMessage.MEMBER_NOT_FOUNT.getExceptionMessage()));

        return TeamDto.ResponseTeamHomeConditions.builder()
                .isExistMyTeam(isPresentTeamWithMe(member))
                .isUnivVerifiedMember(member.isVerifiedMember())
                .build();
    }

    private boolean isPresentTeamWithMe(Member member) {
        return teamMemberRepository.existsByMember(member);
    }

    @Override
    public HashMap<Long, TeamDto.ResponseRecommendationTeamInfo> getRecommendTeams(Long nextIdx, String memberId) {
        List<Team> randomTeams = getRecommendationTeams(nextIdx, memberId);
        LinkedHashMap<Long, TeamDto.ResponseRecommendationTeamInfo> responseHashMap = new LinkedHashMap<>();

         randomTeams.forEach(randomTeam -> responseHashMap.put(randomTeam.getPk(), TeamDto.ResponseRecommendationTeamInfo.builder()
                 .teamName(randomTeam.getTeamName())
                 .content(randomTeam.getContent())
                 .recruitmentType(randomTeam.getRecruitmentType())
                 .teamImgUrl(s3ImgService.getGroupProfilePicUrl(randomTeam.getProfileImgId()))
                 .build()));

         return responseHashMap;
    }

    private List<Team> getRecommendationTeams(Long nextIdx, String memberId) {
        Long remainNum = getRemainNum();
        PageRequest pageRequest = PageRequest.of(0, PAGE_SIZE);
        List<Team> myTeams = teamMemberRepository.findMyTeams(memberId);

        return teamRepository.getRecommendationTeams(nextIdx, myTeams, remainNum, pageRequest);
    }

    @Override
    public HashMap<Long, TeamDto.ResponseRecommendationTeamForMemberInfo> getRecommendTeamsForMember(Long nextIdx, String memberId) {
        List<Team> randomTeams = getRecommendationTeams(nextIdx, memberId);
        LinkedHashMap<Long, TeamDto.ResponseRecommendationTeamForMemberInfo> responseHashMap = new LinkedHashMap<>();

        randomTeams.forEach(randomTeam -> responseHashMap.put(randomTeam.getPk(), TeamDto.ResponseRecommendationTeamForMemberInfo.builder()
                .teamName(randomTeam.getTeamName())
                .content(randomTeam.getContent())
                .teamImgUrl(s3ImgService.getGroupProfilePicUrl(randomTeam.getProfileImgId()))
                .build()));

        return responseHashMap;
    }

    protected long getRemainNum() {
        return LocalDateTime.now().getSecond() % 2;
    }

    @Override
    public TeamDto.ResponseTeamInfoByName getTeamByName(Long nextIdx, String teamName) {
        PageRequest pageRequest = PageRequest.of(0, PAGE_SIZE);
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
            hasNextData = teamRepository.existsByPkLessThanAndTeamNameContainsAndTeamType(minPk.get(), teamName, TeamType.TEAM);
        }

        return hasNextData;
    }

    @Override
    public HashMap<Long, TeamDto.ResponseTeamByMemberUniv> getTeamWithMemberUniv(Long nextIdx, String memberId){
        Member member = memberRepository.findByMemberId(memberId)
                .orElseThrow(() -> new EntityNotFoundException(ExceptionMessage.MEMBER_NOT_FOUNT.getExceptionMessage()));
        UnivEntity univEntity = verifiedUniversityEmailRepository.findUnivEntityByMember(member)
                .orElseThrow(() -> new EntityNotFoundException(ExceptionMessage.NOT_VERIFIED_UNIV_EMAIL.getExceptionMessage()));
        List<Member> univMates = verifiedUniversityEmailRepository.findUnivMates(univEntity, member);
        List<Team> myTeams = teamMemberRepository.findMyTeams(memberId);

        PageRequest pageRequest = PageRequest.of(0, PAGE_SIZE);
        List<Team> teams = teamRepository.getTeamsByTeamOwners(nextIdx, univMates, myTeams, pageRequest);
        LinkedHashMap<Long, TeamDto.ResponseTeamByMemberUniv> responseDto = new LinkedHashMap<>();

        teams.forEach(team -> responseDto.put(team.getPk(), TeamDto.ResponseTeamByMemberUniv.builder()
                .teamName(team.getTeamName())
                .teamImgUrl(s3ImgService.getGroupProfilePicUrl(team.getProfileImgId()))
                .build()));

        return responseDto;
    }
}
