package com.wemingle.core.domain.team.service;

import com.wemingle.core.domain.img.service.S3ImgService;
import com.wemingle.core.domain.member.entity.BannedTeamMember;
import com.wemingle.core.domain.member.entity.Member;
import com.wemingle.core.domain.member.repository.BannedTeamMemberRepository;
import com.wemingle.core.domain.member.repository.MemberAbilityRepository;
import com.wemingle.core.domain.member.service.MemberService;
import com.wemingle.core.domain.member.vo.MemberSummaryInfoVo;
import com.wemingle.core.domain.memberunivemail.entity.VerifiedUniversityEmail;
import com.wemingle.core.domain.memberunivemail.repository.VerifiedUniversityEmailRepository;
import com.wemingle.core.domain.team.dto.TeamDto;
import com.wemingle.core.domain.team.dto.TeamMemberDto;
import com.wemingle.core.domain.team.entity.Team;
import com.wemingle.core.domain.team.entity.TeamMember;
import com.wemingle.core.domain.team.repository.TeamMemberRepository;
import com.wemingle.core.global.exception.NotManagerException;
import com.wemingle.core.global.exceptionmessage.ExceptionMessage;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;

@Transactional(readOnly = true)
@RequiredArgsConstructor
@Service
public class TeamMemberService {
    private final TeamMemberRepository teamMemberRepository;
    private final S3ImgService s3ImgService;
    private final MemberAbilityRepository memberAbilityRepository;
    private final VerifiedUniversityEmailRepository verifiedUniversityEmailRepository;
    private final MemberService memberService;
    private final BannedTeamMemberRepository bannedTeamMemberRepository;

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

    public HashMap<Long, TeamMemberDto.ResponseTeamMembers> getTeamMembersInTeam(Long teamId, String memberId){
        List<TeamMember> teamMembersWithoutMe = teamMemberRepository.findWithTeamWithoutMe(teamId, memberId);

        LinkedHashMap<Long, TeamMemberDto.ResponseTeamMembers> responseData = new LinkedHashMap<>();

        teamMembersWithoutMe.forEach(teamMember -> responseData.put(teamMember.getPk(), TeamMemberDto.ResponseTeamMembers.builder()
                .nickname(teamMember.getNickname())
                .imgUrl(s3ImgService.getTeamMemberPreSignedUrl(teamMember.getProfileImg()))
                .build()
        ));

        return responseData;
    }

    public List<String> getTeamMembersImgUrl(List<Long> teamMembersId){
        List<TeamMember> teamMembers = teamMemberRepository.findAllById(teamMembersId);

        return teamMembers.stream()
                .map(teamMember -> s3ImgService.getTeamMemberPreSignedUrl(teamMember.getProfileImg()))
                .toList();
    }

    public TeamMemberDto.ResponseTeamMemberProfile getTeamMemberProfile(Long teamMemberId) {
        TeamMember teamMember = findById(teamMemberId);
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

        return TeamMemberDto.ResponseTeamMemberProfile.builder()
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
        TeamMember teamMember = findById(updateDto.getTeamMemberId());

        teamMember.updateNickname(updateDto.getNickname());
    }

    public boolean isExistOtherManager(Long teamMemberId) {
        TeamMember teamMember = teamMemberRepository.findById(teamMemberId)
                .orElseThrow(() -> new EntityNotFoundException(ExceptionMessage.TEAM_MEMBER_NOT_FOUND.getExceptionMessage()));

        return teamMemberRepository.isExistOtherManagerRole(teamMember.getTeam());
    }

    @Transactional
    public void updateManagerRoleToLower(Long teamMemberId) {
        TeamMember teamMember = findById(teamMemberId);

        teamMember.demoteManagerRole();
    }

    public boolean isManager(Long teamMemberId) {
        TeamMember teamMember = findById(teamMemberId);

        return teamMember.isManager();
    }

    @Transactional
    public void updateParticipantRoleToHigher(Long teamMemberId) {
        if (!isManager(teamMemberId)) {
            throw new NotManagerException();
        }

        TeamMember teamMember = findById(teamMemberId);

        teamMember.promoteParticipantRole();
    }

    @Transactional
    public void blockTeamMember(Long teamMemberId) {
        if (!isManager(teamMemberId)) {
            throw new NotManagerException();
        }

        TeamMember teamMember = findById(teamMemberId);

        teamMember.block();
    }

    public HashMap<Long, TeamMemberDto.ResponseTeamMemberInfo> getAllTeamMembersInfo(Long teamId, String memberId) {
        Member requester = memberService.findByMemberId(memberId);
        List<TeamMember> teamMembers = teamMemberRepository.findByTeam_Pk(teamId);
        LinkedHashMap<Long, TeamMemberDto.ResponseTeamMemberInfo> responseData = new LinkedHashMap<>();

        teamMembers.forEach(teamMember -> responseData.put(teamMember.getPk(), TeamMemberDto.ResponseTeamMemberInfo.builder()
                        .imgUrl(s3ImgService.getTeamMemberPreSignedUrl(teamMember.getProfileImg()))
                        .nickname(teamMember.getNickname())
                        .teamRole(teamMember.getTeamRole())
                        .isMe(teamMember.isMe(requester))
                .build()));

        return responseData;
    }

    public TeamMember findByTeamAndMember_MemberId(Team team, String memberId) {
        return teamMemberRepository.findByTeamAndMember_MemberId(team, memberId)
                .orElseThrow(() -> new EntityNotFoundException(ExceptionMessage.TEAM_MEMBER_NOT_FOUND.getExceptionMessage()));
    }

    public TeamMember findById(Long teamMemberId) {
        return teamMemberRepository.findById(teamMemberId)
                .orElseThrow(() -> new EntityNotFoundException(ExceptionMessage.TEAM_MEMBER_NOT_FOUND.getExceptionMessage()));
    }

    public boolean isBannedInTeam(Long teamId, String memberId) {
        return bannedTeamMemberRepository.findByTeam_PkAndBannedMember_MemberId(teamId, memberId).isPresent();
    }

    public TeamMemberDto.ResponseBanEndDate getBanEndDateInTeam(Long teamId, String memberId) {
        BannedTeamMember bannedTeamMember = bannedTeamMemberRepository.findByTeam_PkAndBannedMember_MemberId(teamId, memberId)
                .orElseThrow(() -> new RuntimeException(ExceptionMessage.UNBANNED_USER.getExceptionMessage()));

        return TeamMemberDto.ResponseBanEndDate.builder()
                .banStartDate(bannedTeamMember.getBannedDate())
                .banEndDate(bannedTeamMember.getBanEndDate())
                .build();
    }

    public HashMap<Long, TeamMemberDto.ResponseTeamMembers> getSearchTeamMembers(Long teamId, String query) {
        List<TeamMember> teamMembers = teamMemberRepository.findByTeam_PkAndNicknameContaining(teamId, query);
        LinkedHashMap<Long, TeamMemberDto.ResponseTeamMembers> responseData = new LinkedHashMap<>();

        teamMembers.forEach(teamMember -> responseData.put(teamMember.getPk(), TeamMemberDto.ResponseTeamMembers.builder()
                .nickname(teamMember.getNickname())
                .imgUrl(s3ImgService.getTeamMemberPreSignedUrl(teamMember.getProfileImg()))
                .build()));
        return responseData;
    }

    public void verifyBlockTeamMember(TeamMember teamMember) {
        if (teamMember.isBlocked()) {
            throw new RuntimeException(ExceptionMessage.BLOCKED_TEAM_MEMBER.getExceptionMessage());
        }
    }
}
