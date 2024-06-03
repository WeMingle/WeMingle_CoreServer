package com.wemingle.core.domain.matching.service;

import com.wemingle.core.domain.img.service.S3ImgService;
import com.wemingle.core.domain.matching.dto.TeamRequestDto;
import com.wemingle.core.domain.matching.entity.TeamRequest;
import com.wemingle.core.domain.matching.repository.TeamRequestRepository;
import com.wemingle.core.domain.member.entity.Member;
import com.wemingle.core.domain.member.repository.MemberAbilityRepository;
import com.wemingle.core.domain.member.repository.MemberRepository;
import com.wemingle.core.domain.member.vo.MemberSummaryInfoVo;
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
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;

@Slf4j
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

    private final static String IS_NOT_PUBLIC = "비공개";

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

        MemberSummaryInfoVo memberSummaryInfoVo = MemberSummaryInfoVo.builder()
                .member(requester)
                .findAbility(findAbility)
                .univName(verifiedUniversity.getUnivName().getUnivName())
                .build();

        return TeamRequestDto.ResponseRequesterInfo.builder()
                .imgUrl(s3ImgService.getMemberProfilePicUrl(requester.getProfileImgId()))
                .matchingCnt(requester.getCompletedMatchingCnt())
                .nickname(requester.getNickname())
                .univName(memberSummaryInfoVo.getUnivName())
                .gender(memberSummaryInfoVo.getGender())
                .ability(memberSummaryInfoVo.getAbility())
                .majorArea(memberSummaryInfoVo.getMajorArea())
                .age(memberSummaryInfoVo.getAge())
                .reportCnt(memberSummaryInfoVo.getReportCnt())
                .teamQuestionnaires(getTeamQuestions(teamQuestions))
                .build();
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

        teamRequestRepository.save(TeamRequest.builder()
                .nickname(requestSaveDto.getNickname())
                .profileImg(requestSaveDto.getProfilePicId())
                .requester(requester)
                .team(team)
                .build());
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

        return (!isExceedTeamMemberCnt(team) && team.isCapacityLimit()) || !team.isCapacityLimit();
    }

    private boolean isExceedTeamMemberCnt(Team team) {
        return team.getTeamMembers().size() >= team.getCapacityLimit();
    }

    public boolean isRequestApprovable(Long teamPk, List<Long> teamRequestsPk){
        Team team = teamRepository.findById(teamPk)
                .orElseThrow(() -> new EntityNotFoundException(ExceptionMessage.TEAM_NOT_FOUND.getExceptionMessage()));

        return (!isExceedCapacityLimit(team, teamRequestsPk.size()) && team.isCapacityLimit())
                || !team.isCapacityLimit();
    }

    private boolean isExceedCapacityLimit(Team team, int addTeamMemberCnt) {
        return team.getTeamMembers().size() + addTeamMemberCnt > team.getCapacityLimit();
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

    @Transactional
    public void approveTeamRequest(List<Long> teamRequestsPk) {
        List<TeamRequest> teamRequests = teamRequestRepository.findAllById(teamRequestsPk);
        teamRequestRepository.deleteAllInBatch(teamRequests);

        teamRequests.forEach(TeamRequest::addTeamMemberInTeam);
    }

    public TeamRequestDto.ResponseTeamRequestInfo getTeamRequestInfo(Long teamRequestPk){
        TeamRequest teamRequest = teamRequestRepository.findById(teamRequestPk)
                .orElseThrow(() -> new EntityNotFoundException(ExceptionMessage.TEAM_REQUEST_NOT_FOUND.getExceptionMessage()));
        Member requester = teamRequest.getRequester();
        Team team = teamRequest.getTeam();

        List<TeamQuestionnaire> teamQuestions = teamQuestionnaireRepository.findAllByTeam(team);
        List<TeamQuestionnaireAnswer> answers = teamQuestionnaireAnswerRepository.findByTeamQuestionnaireIn(teamQuestions);
        String findAbility = memberAbilityRepository.findAbilityByMemberAndSport(requester, team.getSportsCategory())
                .orElse(null);
        VerifiedUniversityEmail verifiedUniversity = verifiedUniversityEmailRepository.findByMemberFetchUniv(requester)
                .orElseThrow(() -> new EntityNotFoundException(ExceptionMessage.NOT_VERIFIED_UNIV_EMAIL.getExceptionMessage()));

        MemberSummaryInfoVo memberSummaryInfoVo = MemberSummaryInfoVo.builder()
                .member(requester)
                .findAbility(findAbility)
                .univName(verifiedUniversity.getUnivName().getUnivName())
                .build();

        return TeamRequestDto.ResponseTeamRequestInfo.builder()
                .imgUrl(s3ImgService.getMemberProfilePicUrl(requester.getProfileImgId()))
                .matchingCnt(requester.getCompletedMatchingCnt())
                .nickname(requester.getNickname())
                .univName(memberSummaryInfoVo.getUnivName())
                .gender(memberSummaryInfoVo.getGender())
                .ability(memberSummaryInfoVo.getAbility())
                .majorArea(memberSummaryInfoVo.getMajorArea())
                .age(memberSummaryInfoVo.getAge())
                .reportCnt(memberSummaryInfoVo.getReportCnt())
                .isApprovable(team.isAbleToAddMember())
                .teamQuestionnaires(getTeamQuestions(teamQuestions, answers))
                .build();
    }

    private HashMap<String, String> getTeamQuestions(List<TeamQuestionnaire> questionnaires, List<TeamQuestionnaireAnswer> answers){
        LinkedHashMap<String, String> responseData = new LinkedHashMap<>();
        questionnaires.forEach(question -> responseData.put(question.getContent(), getAnswerByTeamQuestion(question, answers)));
        return responseData;
    }

    private String getAnswerByTeamQuestion(TeamQuestionnaire questionnaires, List<TeamQuestionnaireAnswer> answers) {
        return answers.stream()
                .filter(answer -> questionnaires.equals(answer.getTeamQuestionnaire()))
                .map(TeamQuestionnaireAnswer::getAnswer)
                .findFirst()
                .orElse(null);
    }

    public TeamRequestDto.ResponsePendingTeamRequestCnt getPendingTeamRequestCnt(Long teamPk) {
        return TeamRequestDto.ResponsePendingTeamRequestCnt.builder()
                .pendingCnt(teamRequestRepository.findPendingTeamRequestByTeamCnt(teamPk))
                .build();
    }
}
