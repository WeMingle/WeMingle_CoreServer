package com.wemingle.core.domain.matching.service;

import com.wemingle.core.domain.img.service.S3ImgService;
import com.wemingle.core.domain.matching.dto.TeamRequestDto;
import com.wemingle.core.domain.member.entity.Member;
import com.wemingle.core.domain.member.repository.MemberAbilityRepository;
import com.wemingle.core.domain.member.repository.MemberRepository;
import com.wemingle.core.domain.memberunivemail.entity.VerifiedUniversityEmail;
import com.wemingle.core.domain.memberunivemail.repository.VerifiedUniversityEmailRepository;
import com.wemingle.core.domain.team.entity.Team;
import com.wemingle.core.domain.team.repository.TeamQuestionnaireRepository;
import com.wemingle.core.domain.team.repository.TeamRepository;
import com.wemingle.core.global.exceptionmessage.ExceptionMessage;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class TeamRequestService {
    private final MemberRepository memberRepository;
    private final TeamQuestionnaireRepository teamQuestionnaireRepository;
    private final MemberAbilityRepository memberAbilityRepository;
    private final TeamRepository teamRepository;
    private final S3ImgService s3ImgService;
    private final VerifiedUniversityEmailRepository verifiedUniversityEmailRepository;

    public TeamRequestDto.ResponseRequesterInfo getTeamRequestPageInfo(Long teamPk, String memberId){
        Member requester = memberRepository.findByMemberId(memberId)
                .orElseThrow(() -> new EntityNotFoundException(ExceptionMessage.MEMBER_NOT_FOUNT.getExceptionMessage()));
        Team team = teamRepository.findById(teamPk)
                .orElseThrow(() -> new EntityNotFoundException(ExceptionMessage.TEAM_NOT_FOUND.getExceptionMessage()));
        List<String> teamQuestions = teamQuestionnaireRepository.findContentByTeam(team);
        String findAbility = memberAbilityRepository.findAbilityByMemberAndSport(requester, team.getSportsCategory())
                .orElse(null);
        VerifiedUniversityEmail verifiedUniversity = verifiedUniversityEmailRepository.findByMemberFetchUniv(requester)
                .orElseThrow(() -> new EntityNotFoundException(ExceptionMessage.NOT_VERIFIED_UNIV_EMAIL.getExceptionMessage()));

        return TeamRequestDto.ResponseRequesterInfo.builder()
                .imgUrl(s3ImgService.getMemberProfilePicUrl(requester.getProfileImgId()))
                .matchingCnt(requester.getCompletedMatchingCnt())
                .nickname(requester.getNickname())
                .univName(verifiedUniversity.getUnivName().getUnivName())
                .gender(requester.getGender())
                .ability(getAbility(requester, findAbility))
                .majorArea(getMajorArea(requester))
                .age(getMemberAge(requester))
                .reportCnt(requester.getComplaintsCount())
                .teamQuestionnaires(teamQuestions)
                .build();
    }

    private String getMemberAge(Member member){
        return member.isBirthYearPublic() ? String.valueOf(member.getBirthYear()) : "미공개";
    }

    private String getMajorArea(Member member){
        return member.isMajorActivityAreaPublic() ? member.getMajorActivityArea().toString() : "미공개";
    }

    private String getAbility(Member member, String findAbility){
        return member.isAbilityPublic() ? findAbility : "미공개";
    }
}
