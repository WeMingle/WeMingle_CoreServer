package com.wemingle.core.domain.team.service;

import com.wemingle.core.domain.img.service.S3ImgService;
import com.wemingle.core.domain.member.dto.TeamMemberDto;
import com.wemingle.core.domain.member.entity.Member;
import com.wemingle.core.domain.member.repository.MemberAbilityRepository;
import com.wemingle.core.domain.member.vo.MemberSummaryInfoVo;
import com.wemingle.core.domain.memberunivemail.entity.VerifiedUniversityEmail;
import com.wemingle.core.domain.memberunivemail.repository.VerifiedUniversityEmailRepository;
import com.wemingle.core.domain.team.dto.TeamDto;
import com.wemingle.core.domain.team.entity.TeamMember;
import com.wemingle.core.domain.team.repository.TeamMemberRepository;
import com.wemingle.core.global.exceptionmessage.ExceptionMessage;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;

@RequiredArgsConstructor
@Service
public class TeamMemberService {
    private final TeamMemberRepository teamMemberRepository;
    private final S3ImgService s3ImgService;
    private final MemberAbilityRepository memberAbilityRepository;
    private final VerifiedUniversityEmailRepository verifiedUniversityEmailRepository;
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

    public TeamMemberDto.ResponseTeamMemberInfo getTeamMemberInfo(Long teamMemberPk) {
        TeamMember teamMember = teamMemberRepository.findById(teamMemberPk)
                .orElseThrow(() -> new EntityNotFoundException(ExceptionMessage.TEAM_MEMBER_NOT_FOUND.getExceptionMessage()));
        Member requester = teamMember.getMember();
        String findAbility = memberAbilityRepository.findAbilityByMemberAndSport(requester, teamMember.getTeam().getSportsCategory())
                .orElse(null);
        VerifiedUniversityEmail verifiedUniversity = verifiedUniversityEmailRepository.findByMemberFetchUniv(requester)
                .orElseThrow(() -> new EntityNotFoundException(ExceptionMessage.NOT_VERIFIED_UNIV_EMAIL.getExceptionMessage()));

        MemberSummaryInfoVo memberSummaryInfoVo = MemberSummaryInfoVo.builder()
                .member(requester)
                .findAbility(findAbility)
                .univName(verifiedUniversity.getUnivName().getUnivName())
                .build();

        return TeamMemberDto.ResponseTeamMemberInfo.builder()
                .imgUrl(s3ImgService.getTeamMemberPreSignedUrl(teamMember.getProfileImg()))
                .nickname(teamMember.getNickname())
                .introduction(requester.getOneLineIntroduction())
                .matchingCnt(requester.getCompletedMatchingCnt())
                .memberSummaryInfoVo(memberSummaryInfoVo)
                .createdTime(teamMember.getCreatedTime().toLocalDate())
                .build();
    }

    @Transactional
    public void updateTeamMemberProfile(TeamMemberDto.RequestTeamMemberProfileUpdate updateDto) {
        TeamMember teamMember = teamMemberRepository.findById(updateDto.getTeamMemberPk())
                .orElseThrow(() -> new EntityNotFoundException(ExceptionMessage.TEAM_MEMBER_NOT_FOUND.getExceptionMessage()));

        teamMember.updateNickname(updateDto.getNickname());
    }

    public boolean isExistOtherManager(Long teamMemberPk) {
        TeamMember teamMember = teamMemberRepository.findById(teamMemberPk)
                .orElseThrow(() -> new EntityNotFoundException(ExceptionMessage.TEAM_MEMBER_NOT_FOUND.getExceptionMessage()));

        return teamMemberRepository.isExistOtherManagerRole(teamMember.getTeam());
    }

    @Transactional
    public void updateManagerRoleToLower(Long teamMemberPk) {
        TeamMember teamMember = teamMemberRepository.findById(teamMemberPk)
                .orElseThrow(() -> new EntityNotFoundException(ExceptionMessage.TEAM_MEMBER_NOT_FOUND.getExceptionMessage()));

        teamMember.demoteManagerRole();
    }

    public boolean isManager(Long teamMemberPk) {
        TeamMember teamMember = teamMemberRepository.findById(teamMemberPk)
                .orElseThrow(() -> new EntityNotFoundException(ExceptionMessage.TEAM_MEMBER_NOT_FOUND.getExceptionMessage()));

        return teamMember.isManager();
    }

    @Transactional
    public void updateParticipantRoleToHigher(Long teamMemberPk) {
        TeamMember teamMember = teamMemberRepository.findById(teamMemberPk)
                .orElseThrow(() -> new EntityNotFoundException(ExceptionMessage.TEAM_MEMBER_NOT_FOUND.getExceptionMessage()));

        teamMember.promoteParticipantRole();
    }
}
