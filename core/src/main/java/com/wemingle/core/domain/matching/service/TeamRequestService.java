package com.wemingle.core.domain.matching.service;

import com.wemingle.core.domain.img.service.S3ImgService;
import com.wemingle.core.domain.matching.dto.TeamRequestDto;
import com.wemingle.core.domain.matching.entity.TeamRequest;
import com.wemingle.core.domain.matching.repository.TeamRequestRepository;
import com.wemingle.core.domain.member.entity.Member;
import com.wemingle.core.domain.member.repository.MemberAbilityRepository;
import com.wemingle.core.domain.member.repository.MemberRepository;
import com.wemingle.core.domain.memberunivemail.entity.VerifiedUniversityEmail;
import com.wemingle.core.domain.memberunivemail.repository.VerifiedUniversityEmailRepository;
import com.wemingle.core.domain.team.entity.Team;
import com.wemingle.core.domain.team.entity.TeamQuestionnaire;
import com.wemingle.core.domain.team.entity.TeamQuestionnaireAnswer;
import com.wemingle.core.domain.team.entity.recruitmenttype.RecruitmentType;
import com.wemingle.core.domain.team.repository.TeamMemberRepository;
import com.wemingle.core.domain.team.repository.TeamQuestionnaireAnswerRepository;
import com.wemingle.core.domain.team.repository.TeamQuestionnaireRepository;
import com.wemingle.core.domain.team.repository.TeamRepository;
import com.wemingle.core.global.exceptionmessage.ExceptionMessage;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class TeamRequestService {
    private final MemberRepository memberRepository;
    private final TeamQuestionnaireRepository teamQuestionnaireRepository;
    private final MemberAbilityRepository memberAbilityRepository;
    private final TeamRepository teamRepository;
    private final S3ImgService s3ImgService;
    private final VerifiedUniversityEmailRepository verifiedUniversityEmailRepository;
    private final TeamQuestionnaireAnswerRepository teamQuestionnaireAnswerRepository;
    private final TeamRequestRepository teamRequestRepository;
    private final TeamMemberRepository teamMemberRepository;

    private final static String IS_NOT_PUBLIC = "미공개";

    public TeamRequestDto.ResponseRequesterInfo getTeamRequestPageInfo(Long teamPk, String memberId){
        Member requester = memberRepository.findByMemberId(memberId)
                .orElseThrow(() -> new EntityNotFoundException(ExceptionMessage.MEMBER_NOT_FOUNT.getExceptionMessage()));
        Team team = teamRepository.findById(teamPk)
                .orElseThrow(() -> new EntityNotFoundException(ExceptionMessage.TEAM_NOT_FOUND.getExceptionMessage()));
        List<TeamQuestionnaire> teamQuestions = teamQuestionnaireRepository.findActiveByTeam(team);
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
                .teamQuestionnaires(getTeamQuestions(teamQuestions))
                .build();
    }

    private String getMemberAge(Member member){
        return member.isBirthYearPublic() ? String.valueOf(member.getBirthYear()) : IS_NOT_PUBLIC;
    }

    private String getMajorArea(Member member){
        return member.isMajorActivityAreaPublic() ? member.getMajorActivityArea().toString() : IS_NOT_PUBLIC;
    }

    private String getAbility(Member member, String findAbility){
        return member.isAbilityPublic() ? findAbility : IS_NOT_PUBLIC;
    }

    private HashMap<Long, String> getTeamQuestions(List<TeamQuestionnaire> questionnaires){
        LinkedHashMap<Long, String> responseData = new LinkedHashMap<>();
        questionnaires.forEach(question -> responseData.put(question.getPk(), question.getContent()));
        return responseData;
    }

    @Transactional
    public void saveTeamMemberOrRequestByRecruitmentType(TeamRequestDto.RequestTeamRequestSave requestSaveDto, String memberId){
        Member requester = memberRepository.findByMemberId(memberId)
                .orElseThrow(() -> new EntityNotFoundException(ExceptionMessage.MEMBER_NOT_FOUNT.getExceptionMessage()));
        Team team = teamRepository.findById(requestSaveDto.getTeamPk())
                .orElseThrow(() -> new EntityNotFoundException(ExceptionMessage.TEAM_NOT_FOUND.getExceptionMessage()));

        if (team.getRecruitmentType().equals(RecruitmentType.APPROVAL_BASED)){
            saveTeamRequest(requestSaveDto, requester, team);
        } else {
            teamMemberRepository.save(requestSaveDto.of(requester, team));
        }
    }

    private void saveTeamRequest(TeamRequestDto.RequestTeamRequestSave requestSaveDto, Member requester, Team team) {
        if (requestSaveDto.getAnswers() != null) {
            if (!requestSaveDto.getAnswers().isEmpty()) {
                saveTeamQuestionnairesAnswers(requestSaveDto);
            }
        }

        teamRequestRepository.save(TeamRequest.builder().requester(requester).team(team).build());
    }

    private void saveTeamQuestionnairesAnswers(TeamRequestDto.RequestTeamRequestSave requestSaveDto) {
        HashMap<Long, String> questionsAnswer = requestSaveDto.getAnswers();
        List<TeamQuestionnaire> teamQuestionnaires = teamQuestionnaireRepository.findAllById(questionsAnswer.keySet());

        List<TeamQuestionnaireAnswer> teamQuestionnaireAnswers = new ArrayList<>();
        questionsAnswer.keySet().forEach(requestTeamQuestionnaire ->
        {
            TeamQuestionnaire filteredTeamQuestionnaire = teamQuestionnaires.stream()
                    .filter(teamQuestionnaire -> teamQuestionnaire.getPk().equals(requestTeamQuestionnaire))
                    .findFirst()
                    .orElseThrow(() -> new RuntimeException(ExceptionMessage.INVALID_TEAM__QUESTIONNAIRE_PK.getExceptionMessage()));

            TeamQuestionnaireAnswer teamQuestionnaireAnswer = TeamQuestionnaireAnswer.builder()
                    .teamQuestionnaire(filteredTeamQuestionnaire)
                    .answer(questionsAnswer.get(filteredTeamQuestionnaire.getPk()))
                    .build();

            teamQuestionnaireAnswers.add(teamQuestionnaireAnswer);
        });
        teamQuestionnaireAnswerRepository.saveAll(teamQuestionnaireAnswers);
    }

    public boolean isRequestableTeam(Long teamPk){
        Team team = teamRepository.findById(teamPk)
                .orElseThrow(() -> new EntityNotFoundException(ExceptionMessage.TEAM_NOT_FOUND.getExceptionMessage()));

        return !isExceedTeamMemberCnt(team) && team.getRecruitmentType().equals(RecruitmentType.FIRST_SERVED_BASED);
    }

    private static boolean isExceedTeamMemberCnt(Team team) {
        return team.getTeamMembers().size() >= team.getCapacityLimit();
    }

    public TeamRequestDto.ResponseTeamRequests getPendingTeamRequests(Long teamPk) {
        Team team = teamRepository.findById(teamPk)
                .orElseThrow(() -> new EntityNotFoundException(ExceptionMessage.TEAM_NOT_FOUND.getExceptionMessage()));
        List<TeamRequest> teamRequests = teamRequestRepository.findByTeamFetchMember(team);
        LinkedHashMap<Long, TeamRequestDto.RequesterSummary> requesterSummary = new LinkedHashMap<>();

        teamRequests.forEach(teamRequest -> requesterSummary.put(teamRequest.getPk(), TeamRequestDto.RequesterSummary.builder()
                .nickname(teamRequest.getRequester().getNickname())
                .imgUrl(s3ImgService.getMemberProfilePicUrl(teamRequest.getRequester().getProfileImgId()))
                .matchingCnt(teamRequest.getRequester().getCompletedMatchingCnt())
                .createdTime(teamRequest.getCreatedTime())
                .build()));

        return TeamRequestDto.ResponseTeamRequests.builder()
                .remainCapacity(team.getRemainCapacity())
                .requesterSummaries(requesterSummary)
                .build();
    }

    @Transactional
    public void deleteTeamRequest(TeamRequestDto.RequestTeamRequestDelete deleteDto) {
        teamRequestRepository.deleteAllByIdInBatch(deleteDto.getTeamRequestPk());
    }
}
