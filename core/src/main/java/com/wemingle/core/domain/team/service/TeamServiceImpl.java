package com.wemingle.core.domain.team.service;

import com.wemingle.core.domain.category.sports.entity.sportstype.SportsType;
import com.wemingle.core.domain.img.service.S3ImgService;
import com.wemingle.core.domain.matching.repository.TeamRequestRepository;
import com.wemingle.core.domain.member.entity.Member;
import com.wemingle.core.domain.member.repository.MemberRepository;
import com.wemingle.core.domain.memberunivemail.entity.VerifiedUniversityEmail;
import com.wemingle.core.domain.memberunivemail.repository.VerifiedUniversityEmailRepository;
import com.wemingle.core.domain.post.entity.MatchingPost;
import com.wemingle.core.domain.post.entity.gender.Gender;
import com.wemingle.core.domain.post.repository.MatchingPostRepository;
import com.wemingle.core.domain.rating.repository.TeamRatingRepository;
import com.wemingle.core.domain.review.repository.TeamReviewRepository;
import com.wemingle.core.domain.team.dto.CreateTeamDto;
import com.wemingle.core.domain.team.dto.TeamDto;
import com.wemingle.core.domain.team.entity.Team;
import com.wemingle.core.domain.team.entity.TeamMember;
import com.wemingle.core.domain.team.entity.TeamQuestionnaire;
import com.wemingle.core.domain.team.entity.recruitmenttype.RecruitmentType;
import com.wemingle.core.domain.team.entity.teamtype.TeamType;
import com.wemingle.core.domain.team.repository.TeamMemberRepository;
import com.wemingle.core.domain.team.repository.TeamQuestionnaireRepository;
import com.wemingle.core.domain.team.repository.TeamRepository;
import com.wemingle.core.domain.univ.entity.UnivEntity;
import com.wemingle.core.global.exceptionmessage.ExceptionMessage;
import com.wemingle.core.global.util.teamrating.TeamRatingUtil;
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
    private final TeamQuestionnaireRepository teamQuestionnaireRepository;
    private final VerifiedUniversityEmailRepository verifiedUniversityEmailRepository;
    private final TeamReviewRepository teamReviewRepository;
    private final TeamRatingRepository teamRatingRepository;
    private final TeamRequestRepository teamRequestRepository;
    private final MatchingPostRepository matchingPostRepository;
    private static final int PAGE_SIZE = 30;
    @Override
    public HashMap<Long, TeamDto.ResponseWritableTeamInfoDto> getTeamInfoWithAvailableWrite(SportsType sportsType, String memberId) {
        Member member = memberRepository.findByMemberId(memberId)
                .orElseThrow(() -> new EntityNotFoundException(ExceptionMessage.MEMBER_NOT_FOUNT.getExceptionMessage()));
        List<Team> teamList = teamMemberRepository.findTeamsWithAvailableWrite(sportsType, member);

        HashMap<Long, TeamDto.ResponseWritableTeamInfoDto> responseTeamInfo = new HashMap<>();

        teamList.forEach(team -> responseTeamInfo.put(team.getPk(),
                        TeamDto.ResponseWritableTeamInfoDto.builder()
                                .teamName(getNickname(team, member))
                                .teamImgUrl(getTeamImgUrl(team))
                                .teamType(team.getTeamType())
                                .build()));

        return responseTeamInfo;
    }

    private String getTeamImgUrl(Team team) {
        return team.getTeamType().equals(TeamType.INDIVIDUAL)
                ? s3ImgService.getMemberProfilePicUrl(team.getTeamOwner().getProfileImgId())
                : s3ImgService.getGroupProfilePicUrl(team.getProfileImgId());
    }

    private String getNickname(Team team, Member member) {
        return team.getTeamType().equals(TeamType.INDIVIDUAL)
                ? member.getNickname()
                : team.getTeamName();
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
    public HashMap<Long, TeamDto.ResponseTeamInfoInSearch> getTeamByName(Long nextIdx, String teamName) {
        List<Team> teams = teamRepository.getTeamByTeamName(nextIdx, teamName);

        LinkedHashMap<Long, TeamDto.ResponseTeamInfoInSearch> teamInfoHashMap = new LinkedHashMap<>();
        teams.forEach(team -> teamInfoHashMap.put(team.getPk(), TeamDto.ResponseTeamInfoInSearch.builder()
                .teamName(team.getTeamName())
                .content(team.getContent())
                .recruitmentType(team.getRecruitmentType())
                .teamImgUrl(s3ImgService.getGroupProfilePicUrl(team.getProfileImgId()))
                .build()
        ));

        return teamInfoHashMap;
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

    @Override
    public TeamDto.TeamInfo getTeamInfoWithTeam(Long teamPk, String memberId) {
        TeamRatingUtil teamRatingUtil = new TeamRatingUtil();
        Team team = teamRepository.findById(teamPk)
                .orElseThrow(() -> new EntityNotFoundException(ExceptionMessage.TEAM_NOT_FOUND.getExceptionMessage()));
        Optional<TeamMember> teamMember = teamMemberRepository.findByTeamAndMember_MemberId(team, memberId);
        Integer reviewCnt = teamReviewRepository.findTeamReviewCntWithReviewee(team);
        Double totalRating = teamRatingRepository.findTotalRatingWithTeam(team);

        return TeamDto.TeamInfo.builder()
                .createDate(team.getCreatedTime().toLocalDate())
                .teamMemberCnt(team.getTeamMembers().size())
                .teamImgUrl(s3ImgService.getGroupProfilePicUrl(team.getProfileImgId()))
                .teamBackgroundImgUrl(s3ImgService.getTeamBackgroundPreSignedUrl(team.getBackgroundImgId()))
                .teamName(team.getTeamName())
                .teamRating(teamRatingUtil.adjustTeamRating(getNonNullTotalRating(totalRating)))
                .reviewCnt(getNonNullReviewCnt(reviewCnt))
                .content(team.getContent())
                .isManager(isManager(teamMember))
                .build();
    }

    private double getNonNullTotalRating(Double totalRating){
        return totalRating == null ? 0 : totalRating;
    }
    private int getNonNullReviewCnt(Integer reviewCnt) {
        return reviewCnt == null ? 0 : reviewCnt;
    }
    private boolean isManager(Optional<TeamMember> teamMember){
        return teamMember.isPresent() ? teamMember.get().isManager() : false;
    }


    @Transactional
    @Override
    public void saveTeam(String ownerId, CreateTeamDto createTeamDto) {
        Member owner = memberRepository.findByMemberId(ownerId)
                .orElseThrow(() -> new RuntimeException(ExceptionMessage.MEMBER_NOT_FOUNT.getExceptionMessage()));
        Team team = Team.builder().teamOwner(owner)
                .teamName(createTeamDto.getTeamName())
                .content(createTeamDto.getContent())
                .teamType(TeamType.TEAM)
                .sportsCategory(createTeamDto.getSportsType())
                .profileImgId(createTeamDto.getTeamImgId())
                .capacityLimit(createTeamDto.getPersonnelLimitIrrelevant() ? 0 : createTeamDto.getPersonnelLimit())
                .recruitmentType(createTeamDto.getRecruitmentType())
                .startAge(createTeamDto.getAgeIsIrrelevant() ? 0 : createTeamDto.getStartAge())
                .endAge(createTeamDto.getAgeIsIrrelevant() ? 0 : createTeamDto.getEndAge())
                .gender(createTeamDto.getGenderIsIrrelevant() ? null : createTeamDto.getGender())
                .onlySameUniv(createTeamDto.getOnlySameUniv())
                .build();
        teamRepository.save(team);
        List<TeamQuestionnaire> questionnaireList = createTeamDto.getFreeQuestionList().stream()
                .map(question -> TeamQuestionnaire.builder().content(question).team(team).build())
                .toList();
        teamQuestionnaireRepository.saveAll(questionnaireList);
    }

    @Override
    public TeamDto.ResponseTeamParticipantCond getTeamParticipantCond(Long teamPk, String memberId) {
        Member member = memberRepository.findByMemberId(memberId)
                .orElseThrow(() -> new EntityNotFoundException(ExceptionMessage.MEMBER_NOT_FOUNT.getExceptionMessage()));
        Team team = teamRepository.findById(teamPk)
                .orElseThrow(() -> new EntityNotFoundException(ExceptionMessage.TEAM_NOT_FOUND.getExceptionMessage()));
        Optional<TeamMember> teamMember = teamMemberRepository.findByTeamAndMember(team, member);
        UnivEntity teamOwnerUniv = verifiedUniversityEmailRepository.findUnivEntityByMember(team.getTeamOwner())
                .orElseThrow(() -> new EntityNotFoundException(ExceptionMessage.NOT_VERIFIED_UNIV_EMAIL.getExceptionMessage()));
        UnivEntity memberUniv = verifiedUniversityEmailRepository.findUnivEntityByMember(member)
                .orElseThrow(() -> new EntityNotFoundException(ExceptionMessage.NOT_VERIFIED_UNIV_EMAIL.getExceptionMessage()));

        return TeamDto.ResponseTeamParticipantCond
                .builder()
                .beforeWriteInfo(member.isBeforeWriteInfo())
                .isTeamMember(teamMember.isPresent())
                .isTeamRequest(teamRequestRepository.existsByTeamAndRequester(team,member))
                .isExceedCapacity(!isRequestableTeam(team))
                .univCondResult(createUnivCondResult(team.isOnlySameUniv(), memberUniv, teamOwnerUniv))
                .genderCondResult(createGenderCondResult(team, member.getGender()))
                .birthYearCondResult(createBirthYearCondResult(team, member.getBirthYear()))
                .build();
    }

    private boolean isRequestableTeam(Team team){
        return !isExceedTeamMemberCnt(team) && team.getRecruitmentType().equals(RecruitmentType.FIRST_SERVED_BASED);
    }

    private static boolean isExceedTeamMemberCnt(Team team) {
        return team.getTeamMembers().size() >= team.getCapacityLimit();
    }

    private Boolean createUnivCondResult(boolean onlySameUniv, UnivEntity memberUniv, UnivEntity teamOwnerUniv) {
        return onlySameUniv ? memberUniv.equals(teamOwnerUniv) : null;
    }

    private TeamDto.GenderCondResult createGenderCondResult(Team team, Gender memberGender) {
        Gender teamGender = team.getGender();

        return team.hasGenderCond()
                ? TeamDto.GenderCondResult.builder()
                    .isSatisfiedGenderCond(teamGender.equals(memberGender))
                    .gender(teamGender)
                    .build()
                : null;
    }

    private TeamDto.BirthYearCondResult createBirthYearCondResult(Team team, int memberBirthYear) {
        int teamCondStartAge = team.getStartAge();
        int teamCondEndAge = team.getEndAge();

        return team.hasAgeCond()
                ? TeamDto.BirthYearCondResult.builder()
                    .isSatisfiedBirthYearCond(teamCondStartAge <= memberBirthYear && teamCondEndAge >= memberBirthYear)
                    .startAge(teamCondStartAge)
                    .endAge(teamCondEndAge)
                    .build()
                : null;
    }

    @Override
    public HashMap<Long, TeamDto.ResponseWritableTeamInfoDto> getRequestableTeamsInfo(Long matchingPostPk, SportsType sportsType, String memberId) {
        Member member = memberRepository.findByMemberId(memberId)
                .orElseThrow(() -> new EntityNotFoundException(ExceptionMessage.MEMBER_NOT_FOUNT.getExceptionMessage()));
        MatchingPost matchingPost = matchingPostRepository.findById(matchingPostPk)
                .orElseThrow(() -> new EntityNotFoundException(ExceptionMessage.POST_NOT_FOUND.getExceptionMessage()));

        List<Team> teamList = teamMemberRepository.findTeamsWithAvailableRequest(member, sportsType, TeamType.valueOf(matchingPost.getRecruiterType().toString()));

        HashMap<Long, TeamDto.ResponseWritableTeamInfoDto> responseTeamInfo = new HashMap<>();

        teamList.forEach(team -> responseTeamInfo.put(team.getPk(),
                TeamDto.ResponseWritableTeamInfoDto.builder()
                        .teamName(getNickname(team, member))
                        .teamImgUrl(getTeamImgUrl(team))
                        .teamType(team.getTeamType())
                        .build()));

        return responseTeamInfo;
    }

    @Override
    public Team findByTeamPk(Long teamPk) {
        return teamRepository.findById(teamPk)
                .orElseThrow(() -> new EntityNotFoundException(ExceptionMessage.TEAM_NOT_FOUND.getExceptionMessage()));
    }

    @Override
    public TeamDto.ResponseTeamSetting getTeamSetting(Long teamPk){
        Team team = teamRepository.findById(teamPk)
                .orElseThrow(() -> new EntityNotFoundException(ExceptionMessage.TEAM_NOT_FOUND.getExceptionMessage()));

        return TeamDto.ResponseTeamSetting.builder()
                .createDate(team.getCreatedTime().toLocalDate())
                .teamMembersCnt(team.getTeamMembers().size())
                .teamImgUrl(s3ImgService.getGroupProfilePicUrl(team.getProfileImgId()))
                .teamBackgroundImgUrl(s3ImgService.getTeamBackgroundPreSignedUrl(team.getBackgroundImgId()))
                .teamName(team.getTeamName())
                .content(team.getContent())
                .recruitmentType(team.getRecruitmentType())
                .univCond(getTeamUnivCond(team))
                .genderCond(getTeamGenderCond(team))
                .birthCond(getTeamBirthCond(team))
                .capacityLimit(team.getCapacityLimit())
                .teamQuestionnaire(getTeamQuestionnaire(team))
                .build();
    }

    public String getTeamUnivCond(Team team){
        VerifiedUniversityEmail verifiedUniversityEmail = verifiedUniversityEmailRepository.findByMemberFetchUniv(team.getTeamOwner())
                .orElseThrow(() -> new EntityNotFoundException(ExceptionMessage.NOT_VERIFIED_UNIV_EMAIL.getExceptionMessage()));

        return team.isOnlySameUniv() ? verifiedUniversityEmail.getUnivName().getUnivName() : null;
    }

    public Gender getTeamGenderCond(Team team){
        return team.hasGenderCond() ? team.getGender() : null;
    }

    public TeamDto.BirthCond getTeamBirthCond(Team team){
        return team.hasAgeCond()
                ? TeamDto.BirthCond.builder().startAge(team.getStartAge()).endAge(team.getEndAge()).build()
                : null;
    }

    public HashMap<Long, String> getTeamQuestionnaire(Team team){
        List<TeamQuestionnaire> teamQuestionnaires = teamQuestionnaireRepository.findActiveByTeam(team);

        LinkedHashMap<Long, String> responseData = new LinkedHashMap<>();
        teamQuestionnaires.forEach(teamQuestionnaire -> responseData.put(teamQuestionnaire.getPk(), teamQuestionnaire.getContent()));

        return responseData;
    }

    @Override
    @Transactional
    public void updateTeamSetting(TeamDto.RequestTeamSettingUpdate updateDto){
        Team team = teamRepository.findById(updateDto.getTeamPk())
                .orElseThrow(() -> new EntityNotFoundException(ExceptionMessage.TEAM_NOT_FOUND.getExceptionMessage()));

        team.updateTeamSetting(updateDto);

        List<Long> deleteQuestionnairePks = updateDto.getDeleteQuestionnairePks();
        if (deleteQuestionnairePks != null && !deleteQuestionnairePks.isEmpty()) {
            List<TeamQuestionnaire> teamQuestionnaires = teamQuestionnaireRepository.findAllById(deleteQuestionnairePks);
            teamQuestionnaires.forEach(TeamQuestionnaire::isDeleted);
        }

        List<String> newQuestionnaires = updateDto.getNewQuestionnaires();
        if (newQuestionnaires != null && !newQuestionnaires.isEmpty()) {
            List<TeamQuestionnaire> saveNewQuestionnaires = newQuestionnaires.stream().map(questionnaire -> TeamQuestionnaire.builder()
                            .team(team)
                            .content(questionnaire)
                            .build())
                    .toList();

            teamQuestionnaireRepository.saveAll(saveNewQuestionnaires);
        }
    }

    @Override
    public HashMap<Long, TeamDto.Response15PopularTeamInfo> get15PopularTeamOrIndividual() {
        LinkedHashMap<Long, TeamDto.Response15PopularTeamInfo> responseData = new LinkedHashMap<>();

        List<Team> popularTeamAndIndividual = teamRepository.find15PopularTeamOrIndividual();
        popularTeamAndIndividual.forEach(team -> responseData.put(team.getPk(), TeamDto.Response15PopularTeamInfo.builder()
                .nickname(getNickname(team, team.getTeamOwner()))
                .imgUrl(getTeamImgUrl(team))
                .matchingCnt(team.getCompletedMatchingCnt())
                .teamMembersCnt(team.getTeamMembers().size())
                .teamType(team.getTeamType())
                .build())
        );

        return responseData;
    }
}
