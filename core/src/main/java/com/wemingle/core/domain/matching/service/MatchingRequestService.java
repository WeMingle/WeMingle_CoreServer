package com.wemingle.core.domain.matching.service;

import com.wemingle.core.domain.img.service.S3ImgService;
import com.wemingle.core.domain.matching.controller.requesttype.RequestType;
import com.wemingle.core.domain.matching.dto.MatchingRequestDto;
import com.wemingle.core.domain.matching.dto.requesttitlestatus.RequestTitleStatus;
import com.wemingle.core.domain.matching.entity.Matching;
import com.wemingle.core.domain.matching.entity.MatchingRequest;
import com.wemingle.core.domain.matching.repository.MatchingRepository;
import com.wemingle.core.domain.matching.repository.MatchingRequestRepository;
import com.wemingle.core.domain.matching.vo.TitleInfo;
import com.wemingle.core.domain.member.entity.Member;
import com.wemingle.core.domain.member.entity.MemberAbility;
import com.wemingle.core.domain.member.repository.MemberAbilityRepository;
import com.wemingle.core.domain.member.repository.MemberRepository;
import com.wemingle.core.domain.post.entity.MatchingPost;
import com.wemingle.core.domain.post.entity.abillity.Ability;
import com.wemingle.core.domain.post.entity.recruitertype.RecruiterType;
import com.wemingle.core.domain.post.repository.MatchingPostRepository;
import com.wemingle.core.domain.rating.entity.TeamRating;
import com.wemingle.core.domain.rating.repository.TeamRatingRepository;
import com.wemingle.core.domain.team.entity.Team;
import com.wemingle.core.global.exceptionmessage.ExceptionMessage;
import com.wemingle.core.global.util.teamrating.TeamRatingUtil;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import static com.wemingle.core.global.exceptionmessage.ExceptionMessage.MEMBER_NOT_FOUNT;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
@Slf4j
public class MatchingRequestService {
    private final MatchingRepository matchingRepository;
    private final MatchingPostRepository matchingPostRepository;
    private final MemberRepository memberRepository;
    private final MatchingRequestRepository matchingRequestRepository;
    private final S3ImgService s3ImgService;
    private final TeamRatingRepository teamRatingRepository;
    private final MemberAbilityRepository memberAbilityRepository;

    private static final String IS_OWNER_SENT_SUFFIX = "에 매칭 신청을 보냈습니다.";
    private static final String IS_PARTICIPANT_TITLE_PREFIX = "내가 속한 ";
    private static final String IS_PARTICIPANT_SENT_SUFFIX = "이 매칭 신청을 보냈습니다.";
    private static final String RECEIVE_SUFFIX = "이 매칭을 신청했습니다.";
    private static final String COMPLETE_SUFFIX = "과 매칭이 성사되었습니다.";
    private static final String CANCEL_SUFFIX = "과 매칭이 실패하였습니다.";
    private static final int PAGE_SIZE = 30;

    public List<MatchingRequestDto.ResponseMatchingRequestHistory> getMatchingRequestHistories(Long nextIdx,
                                                                                               RequestType requestType,
                                                                                               RecruiterType recruiterType,
                                                                                               boolean excludeCompleteMatchesFilter,
                                                                                               String memberId){
        Member findMember = memberRepository.findByMemberId(memberId)
                .orElseThrow(() -> new EntityNotFoundException(MEMBER_NOT_FOUNT.getExceptionMessage()));
        List<MatchingPost> myMatchingPost = matchingPostRepository.findByWriter_Member(findMember);
        PageRequest pageRequest = PageRequest.of(0, PAGE_SIZE);

        List<MatchingRequest> matchingRequestHistories = matchingRequestRepository.findMatchingRequestHistories(nextIdx, requestType, recruiterType, excludeCompleteMatchesFilter, findMember, myMatchingPost, pageRequest);

        return matchingRequestHistories.stream().map(matchingRequest -> MatchingRequestDto.ResponseMatchingRequestHistory.builder()
                        .titleInfo(createTitleInfo(matchingRequest, myMatchingPost, findMember))
                        .content(matchingRequest.getMatchingPost().getContent())
                        .requestDate(matchingRequest.getCreatedTime().toLocalDate())
                        .matchingStatus(matchingRequest.getMatchingRequestStatus())
                        .matchingRequestPk(matchingRequest.getPk())
                        .matchingPostPk(matchingRequest.getMatchingPost().getPk())
                        .build())
                .toList();
    }

    private TitleInfo createTitleInfo(MatchingRequest matchingRequest, List<MatchingPost> myMatchingPost, Member findMember) {
        String teamName = matchingRequest.getTeam().getTeamName();
        switch (matchingRequest.getMatchingRequestStatus()){
            case COMPLETE -> {
                return new TitleInfo(teamName + COMPLETE_SUFFIX, RequestTitleStatus.COMPLETE);
            }
            case CANCEL -> {
                return new TitleInfo(teamName + CANCEL_SUFFIX, RequestTitleStatus.CANCEL);
            }
            case PENDING -> {
                if (myMatchingPost.contains(matchingRequest.getMatchingPost())){
                    return getTitleInfoWithRecruiterType(matchingRequest, teamName);
                }

                return getTitleInfoWithSender(matchingRequest, findMember, teamName);
            }
            default -> throw new RuntimeException(ExceptionMessage.INVALID_MATCHING_REQUEST_STATUS.getExceptionMessage());
        }
    }

    private static TitleInfo getTitleInfoWithRecruiterType(MatchingRequest matchingRequest, String teamName) {
        return matchingRequest.getMatchingPost().getRecruiterType().equals(RecruiterType.TEAM)
                ? new TitleInfo(teamName + RECEIVE_SUFFIX, RequestTitleStatus.RECEIVE_BY_TEAM)
                : new TitleInfo(teamName + RECEIVE_SUFFIX, RequestTitleStatus.RECEIVE_BY_INDIVIDUAL);
    }

    private static TitleInfo getTitleInfoWithSender(MatchingRequest matchingRequest, Member findMember, String teamName) {
        return matchingRequest.getTeam().getTeamOwner().equals(findMember)
                ? new TitleInfo(teamName + IS_OWNER_SENT_SUFFIX, RequestTitleStatus.SENT_BY_ME)
                : new TitleInfo(IS_PARTICIPANT_TITLE_PREFIX + teamName + IS_PARTICIPANT_SENT_SUFFIX, RequestTitleStatus.SENT_BY_OWNER);
    }

    public MatchingRequestDto.ResponsePendingRequestsByIndividual getPendingRequestsByIndividual(Long matchingPostPk){
        MatchingPost matchingPost = matchingPostRepository.findById(matchingPostPk)
                .orElseThrow(() -> new EntityNotFoundException(ExceptionMessage.POST_NOT_FOUND.getExceptionMessage()));
        List<MatchingRequest> matchingRequests = matchingRequestRepository.findIndividualRequests(matchingPost);
        List<MemberAbility> memberAbilities = memberAbilityRepository.findByMemberInAndSportsType(getRequestMembers(matchingRequests), matchingPost.getSportsCategory());

        LinkedHashMap<Long, MatchingRequestDto.RequestInfoByIndividual> requestsInfo = new LinkedHashMap<>();
        matchingRequests.forEach(matchingRequest -> requestsInfo.put(matchingRequest.getPk(), MatchingRequestDto.RequestInfoByIndividual.builder()
                .profileImg(s3ImgService.getMemberProfilePicUrl(matchingRequest.getMember().getProfileImgId()))
                .nickname(matchingRequest.getMember().getNickname())
                .content(matchingRequest.getContent())
                .completedMatchingCnt(matchingRequest.getTeam().getCompletedMatchingCnt())
                .majorActivityArea(matchingRequest.getMember().getMajorActivityArea())
                .ability(getMemberAbilities(memberAbilities, matchingRequest.getMember()))
                .build()));

        return MatchingRequestDto.ResponsePendingRequestsByIndividual.builder()
                .title(matchingPost.getContent())
                .requestsInfo(requestsInfo)
                .build();
    }

    private Ability getMemberAbilities(List<MemberAbility> memberAbilities, Member member) {
        if (!member.isAbilityPublic()){
            log.info(member.getPk().toString());
            return null;
        }

        return memberAbilities.stream()
                .filter(memberAbility -> memberAbility.getMember().equals(member))
                .findFirst()
                .map(MemberAbility::getAbility)
                .orElse(null);
    }

    private List<Member> getRequestMembers(List<MatchingRequest> matchingRequests) {
        return matchingRequests.stream().map(MatchingRequest::getMember).toList();
    }

    public MatchingRequestDto.ResponsePendingRequestsByTeam getPendingRequestsByTeam(Long matchingPostPk){
        MatchingPost matchingPost = matchingPostRepository.findById(matchingPostPk)
                .orElseThrow(() -> new EntityNotFoundException(ExceptionMessage.POST_NOT_FOUND.getExceptionMessage()));
        List<MatchingRequest> matchingRequests = matchingRequestRepository.findTeamRequestsIsOwner(matchingPost);
        List<Team> teamsInRequests = matchingRequests.stream().map(MatchingRequest::getTeam).toList();
        List<TeamRating> teamRatings = teamRatingRepository.findTeamRatingInPk(teamsInRequests);

        LinkedHashMap<Long, MatchingRequestDto.RequestInfoByTeam> requestsInfo = new LinkedHashMap<>();
        matchingRequests.forEach(matchingRequest -> requestsInfo.put(matchingRequest.getPk(), MatchingRequestDto.RequestInfoByTeam.builder()
                .profileImg(s3ImgService.getGroupProfilePicUrl(matchingRequest.getTeam().getProfileImgId()))
                .teamName(matchingRequest.getTeam().getTeamName())
                .content(matchingRequest.getContent())
                .completedMatchingCnt(matchingRequest.getTeam().getCompletedMatchingCnt())
                .teamMemberCnt(matchingRequest.getTeam().getTeamMembers().size())
                .teamRating(getTeamRating(teamRatings, matchingRequest.getTeam()))
                .build()));

        return MatchingRequestDto.ResponsePendingRequestsByTeam.builder()
                .title(matchingPost.getContent())
                .requestsInfo(requestsInfo)
                .build();
    }

    private double getTeamRating(List<TeamRating> teamRatings, Team team) {
        TeamRatingUtil teamRatingUtil = new TeamRatingUtil();

        double totalRating = teamRatings.stream()
                .filter(teamRating -> teamRating.getTeam().equals(team))
                .findFirst()
                .map(TeamRating::getTotalRating)
                .orElse(0.0);

        return teamRatingUtil.adjustTeamRating(totalRating);
    }

    @Transactional
    public void approveMatchingRequests(MatchingRequestDto.MatchingRequestApprove matchingRequestApprove){
        List<MatchingRequest> matchingAllRequest = getAllMatchingRequests(matchingRequestApprove.getMatchingRequests());
        ArrayList<Matching> saveMatching = new ArrayList<>();

        matchingAllRequest.forEach(matchingRequest -> {
            matchingRequest.completeRequest();
            saveMatching.add(matchingRequest.of(matchingRequest));
        });

        matchingRepository.saveAll(saveMatching);
    }

    @Transactional
    public void deleteMatchingRequests(MatchingRequestDto.MatchingRequestDelete matchingRequestDelete){
        List<MatchingRequest> matchingAllRequests = getAllMatchingRequests(matchingRequestDelete.getMatchingRequests());

        matchingRequestRepository.deleteAllInBatch(matchingAllRequests);
    }

    private List<MatchingRequest> getAllMatchingRequests(List<Long> matchingRequestsPk) {
        List<MatchingRequest> matchingRequests = matchingRequestRepository.findByPkIn(matchingRequestsPk);
        Set<Team> teams = matchingRequests.stream().map(MatchingRequest::getTeam).collect(Collectors.toSet());
        MatchingPost matchingPost = matchingRequests.stream().map(MatchingRequest::getMatchingPost).findFirst()
                .orElseThrow(() -> new EntityNotFoundException(ExceptionMessage.POST_NOT_FOUND.getExceptionMessage()));
        return matchingRequestRepository.findAllRequestsWithTeam(matchingPost, teams);
    }
}
