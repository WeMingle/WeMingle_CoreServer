package com.wemingle.core.domain.post.service;

import com.wemingle.core.domain.bookmark.entity.BookmarkedMatchingPost;
import com.wemingle.core.domain.bookmark.repository.BookmarkRepository;
import com.wemingle.core.domain.img.service.S3ImgService;
import com.wemingle.core.domain.matching.entity.Matching;
import com.wemingle.core.domain.matching.repository.MatchingRepository;
import com.wemingle.core.domain.member.entity.Member;
import com.wemingle.core.domain.member.repository.MemberRepository;
import com.wemingle.core.domain.post.dto.MatchingPostDto;
import com.wemingle.core.domain.post.entity.MatchingPost;
import com.wemingle.core.domain.post.entity.MatchingPostArea;
import com.wemingle.core.domain.post.entity.abillity.Ability;
import com.wemingle.core.domain.post.entity.area.AreaName;
import com.wemingle.core.domain.post.entity.gender.Gender;
import com.wemingle.core.domain.post.entity.matchingstatus.MatchingStatus;
import com.wemingle.core.domain.post.entity.recruitertype.RecruiterType;
import com.wemingle.core.domain.post.repository.MatchingPostAreaRepository;
import com.wemingle.core.domain.post.repository.MatchingPostRepository;
import com.wemingle.core.domain.review.repository.TeamReviewRepository;
import com.wemingle.core.domain.team.entity.Team;
import com.wemingle.core.domain.team.entity.TeamMember;
import com.wemingle.core.domain.team.entity.recruitmenttype.RecruitmentType;
import com.wemingle.core.domain.team.entity.teamtype.TeamType;
import com.wemingle.core.domain.team.repository.TeamMemberRepository;
import com.wemingle.core.domain.team.repository.TeamRepository;
import com.wemingle.core.global.exceptionmessage.ExceptionMessage;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Duration;
import java.time.LocalDate;
import java.time.YearMonth;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.NoSuchElementException;

import static com.wemingle.core.global.exceptionmessage.ExceptionMessage.*;
import static com.wemingle.core.global.matchingstatusdescription.MatchingStatusDescription.*;


@Slf4j
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class MatchingPostService {
    private final MatchingPostRepository matchingPostRepository;
    private final MatchingPostAreaRepository matchingPostAreaRepository;
    private final TeamRepository teamRepository;
    private final TeamMemberRepository teamMemberRepository;
    private final MemberRepository memberRepository;
    private final MatchingRepository matchingRepository;
    private final BookmarkRepository bookmarkRepository;
    private final TeamReviewRepository teamReviewRepository;
    private final S3ImgService s3ImgService;

    @Value("${wemingle.ip}")
    private String serverIp;

    private static final int PERMIT_CANCEL_DURATION = 3;

    public MatchingPost getMatchingPostByPostId(Long postId) {
        return matchingPostRepository.findById(postId)
                .orElseThrow(()->new NoSuchElementException(ExceptionMessage.POST_NOT_FOUND.getExceptionMessage()));
    }

    public Integer getFilteredMatchingPostCnt(String memberId,
                                              Long nextIdx,
                                              RecruitmentType recruitmentType,
                                              Ability ability,
                                              Gender gender,
                                              RecruiterType recruiterType,
                                              List<AreaName> areaList,
                                              LocalDate dateFilter,
                                              YearMonth monthFilter,
                                              Boolean excludeExpired) {
        if (dateFilter != null && monthFilter != null) {
            throw new RuntimeException(DATE_MONTH_CANT_COEXIST.getExceptionMessage());
        }

        return matchingPostRepository.findFilteredMatchingPostCnt(
                nextIdx,
                recruitmentType,
                ability,
                gender,
                recruiterType,
                areaList,
                excludeExpired == null ? null : LocalDate.now(),
                dateFilter,
                monthFilter,
                PageRequest.of(0, 30)
        );
    }

    public HashMap<Long, Object> getFilteredMatchingPost(String memberId,
                                                         Long nextIdx,
                                                         RecruitmentType recruitmentType,
                                                         Ability ability,
                                                         Gender gender,
                                                         RecruiterType recruiterType,
                                                         List<AreaName> areaList,
                                                         LocalDate dateFilter,
                                                         YearMonth monthFilter,
                                                         Boolean excludeExpired){

        if (dateFilter != null && monthFilter != null) {
            throw new RuntimeException(DATE_MONTH_CANT_COEXIST.getExceptionMessage());
        }

        List<MatchingPost> filteredMatchingPost = matchingPostRepository.findFilteredMatchingPost(
                nextIdx,
                recruitmentType,
                ability,
                gender,
                recruiterType,
                areaList,
                excludeExpired == null ? null : LocalDate.now(),
                dateFilter,
                monthFilter,
                PageRequest.of(0, 30)
        );

        List<BookmarkedMatchingPost> bookmarkedByMatchingPosts = bookmarkRepository.findBookmarkedByMatchingPosts(filteredMatchingPost, memberId);

        HashMap<Long, Object> objectNode = new LinkedHashMap<>();

        filteredMatchingPost.forEach(post -> {
                    boolean isBookmarked = bookmarkedByMatchingPosts.stream().anyMatch(bookmark -> bookmark.getMatchingPost().equals(post));

                    objectNode.put(post.getPk(), MatchingPostDto.ResponseMatchingPostDto.builder()
                            .writer(post.getWriter().getTeam().getTeamName())
                            .matchingDate(post.getMatchingDate())
                            .areaList(post.getAreaList().stream().map(MatchingPostArea::getAreaName).toList())
                            .ability(post.getAbility())
                            .isLocationConsensusPossible(post.isLocationConsensusPossible())
                            .contents(post.getContent())
                            .recruiterType(post.getRecruiterType())
                            .profilePicUrl(post.getRecruiterType().equals(RecruiterType.TEAM) ? s3ImgService.getGroupProfilePicUrl(post.getTeam().getProfileImgId()) : s3ImgService.getMemberProfilePicUrl(post.getTeam().getProfileImgId()))
                            .matchingCnt(post.getTeam().getCompletedMatchingCnt())
                            .isBookmarked(isBookmarked)
                            .build());
                }
        );
        return objectNode;
    }

    public LinkedHashMap<Long, MatchingPostDto.ResponseCompletedMatchingPost> getCompletedMatchingPosts(Long nextIdx, RecruiterType recruiterType, boolean excludeCompleteMatchesFilter, String memberId){
        Member member = memberRepository.findByMemberId(memberId).orElseThrow(() -> new EntityNotFoundException(MEMBER_NOT_FOUNT.getExceptionMessage()));
        List<MatchingPost> matchingPostWithMemberId = teamReviewRepository.findMatchingPostWithMemberId(member);
        PageRequest pageRequest = PageRequest.of(0, 30);

        List<MatchingPost> matchingPosts = matchingPostRepository.findCompletedMatchingPosts(nextIdx, recruiterType, excludeCompleteMatchesFilter, member, matchingPostWithMemberId, pageRequest);

        List<MatchingPostArea> matchingPostAreas = matchingPostAreaRepository.findByMatchingPostIn(matchingPosts);

        LinkedHashMap<Long, MatchingPostDto.ResponseCompletedMatchingPost> responseHashMap = new LinkedHashMap<>();

        matchingPosts.forEach(matchingPost -> responseHashMap.put(matchingPost.getPk(), MatchingPostDto.ResponseCompletedMatchingPost.builder()
                .matchingDate(matchingPost.getMatchingDate())
                .recruiterType(matchingPost.getRecruiterType())
                .teamName(matchingPost.getTeam().getTeamName())
                .completedMatchingCnt(matchingPost.getTeam().getCompletedMatchingCnt())
                .content(matchingPost.getContent())
                .areaNames(getAreaNames(matchingPost, matchingPostAreas))
                .isLocationConsensusPossible(matchingPost.isLocationConsensusPossible())
                .ability(matchingPost.getAbility())
                .profileImgUrl(getProfileImgUrl(matchingPost))
                .detailPostUrl(serverIp + DETAIL_POST_URL.getRequestUrl() + matchingPost.getPk())
                .matchingStatus(matchingStatusFactory(matchingPost.getMatchingStatus(), matchingPost.getMatchingDate()))
                .scheduledRequest(getScheduledRequest(matchingPost, member, matchingPostWithMemberId))
                .build()));

        return responseHashMap;
    }

    private static List<AreaName> getAreaNames(MatchingPost matchingPost, List<MatchingPostArea> matchingPostAreas) {
        return matchingPostAreas.stream()
                .filter(matchingPostArea -> matchingPostArea.getMatchingPost().equals(matchingPost))
                .map(MatchingPostArea::getAreaName)
                .toList();
    }

    private String getProfileImgUrl(MatchingPost matchingPost) {
        return isTeam(matchingPost)
                ? s3ImgService.getGroupProfilePicUrl(matchingPost.getTeam().getProfileImgId())
                : s3ImgService.getMemberProfilePicUrl(matchingPost.getTeam().getProfileImgId());
    }

    private static boolean isTeam(MatchingPost matchingPost) {
        return matchingPost.getTeam().getTeamType().equals(TeamType.TEAM);
    }

    private String matchingStatusFactory(MatchingStatus matchingStatus, LocalDate matchingDate) {
        switch (matchingStatus){
            case CANCEL -> {
                return CANCEL_MATCHING.getDescription();
            }
            case COMPLETE -> {
                long remainDays = Duration.between(LocalDate.now().atStartOfDay(), matchingDate.atStartOfDay()).toDays();
                return remainDays > 0 ? REMAIN_DAYS_PREFIX.getDescription() + remainDays : COMPLETE_MATCHING.getDescription();
            }
            default -> throw new RuntimeException(INVALID_MATCHING_POST_STATUS.getExceptionMessage());
        }
    }

    private MatchingPostDto.ScheduledRequest getScheduledRequest(MatchingPost matchingPost, Member member, List<MatchingPost> matchingPostWithMemberId) {
        return isTeamOwner(matchingPost, member)
                ? scheduledRequestFactoryWithOwner(matchingPost.getMatchingStatus(), matchingPost.getMatchingDate(), matchingPost, matchingPostWithMemberId)
                : scheduledRequestFactoryWithMember();
    }

    private static boolean isTeamOwner(MatchingPost matchingPost, Member member) {
        return matchingPost.getTeam().getTeamOwner().equals(member);
    }

    private MatchingPostDto.ScheduledRequest scheduledRequestFactoryWithOwner(MatchingStatus matchingStatus,
                                                                              LocalDate matchingDate,
                                                                              MatchingPost matchingPost,
                                                                              List<MatchingPost> matchingPostWithMemberId) {
        switch (matchingStatus) {
            case CANCEL -> {
                return new MatchingPostDto.ScheduledRequest(RENEW_MATCHING_POST.getDescription(), serverIp + RENEW_MATCHING_POST.getRequestUrl());
            }
            case COMPLETE -> {
                long remainDays = Duration.between(LocalDate.now().atStartOfDay(), matchingDate.atStartOfDay()).toDays();

                if (remainDays >= PERMIT_CANCEL_DURATION) {
                    //todo 채팅 기능 구현되면 requestUrl 변경하기
                    return new MatchingPostDto.ScheduledRequest(CANCEL_BY_CHAT.getDescription(), CANCEL_BY_CHAT.getRequestUrl());
                } else if (remainDays >= 0) {
                    return new MatchingPostDto.ScheduledRequest(CANCEL_NOT_PERMITTED_DURATION.getDescription(), CANCEL_NOT_PERMITTED_DURATION.getRequestUrl());
                } else {
                    return checkReviewWrittenPost(matchingPost, matchingPostWithMemberId);
                }
            }
            default -> throw new RuntimeException(INVALID_MATCHING_POST_STATUS.getExceptionMessage());
        }
    }

    private static MatchingPostDto.ScheduledRequest checkReviewWrittenPost(MatchingPost matchingPost, List<MatchingPost> matchingPostWithMemberId) {
        if (matchingPostWithMemberId.contains(matchingPost)) {
            return new MatchingPostDto.ScheduledRequest(AFTER_WRITE_REVIEW.getDescription(), AFTER_WRITE_REVIEW.getRequestUrl());
        } else {
            return new MatchingPostDto.ScheduledRequest(BEFORE_WRITE_REVIEW.getDescription(), BEFORE_WRITE_REVIEW.getRequestUrl());
        }
    }

    private MatchingPostDto.ScheduledRequest scheduledRequestFactoryWithMember() {
        return new MatchingPostDto.ScheduledRequest(NO_PERMISSION.getDescription(), NO_PERMISSION.getRequestUrl());
    }

    @Transactional
    public void createMatchingPost(MatchingPostDto.CreateMatchingPostDto createMatchingPostDto, String writerId){
        RecruiterType recruiterType = createMatchingPostDto.getRecruiterType();
        Long teamPk = createMatchingPostDto.getTeamPk();
        List<String> participantsId = createMatchingPostDto.getParticipantsId();

        Team team = teamRepository.findById(teamPk).orElseThrow(() -> new EntityNotFoundException(TEAM_NOT_FOUND.getExceptionMessage()));
        TeamMember writerInTeam = teamMemberRepository.findByTeamAndMember_MemberId(team, writerId)
                .orElseThrow(() -> new EntityNotFoundException(TEAM_MEMBER_NOT_FOUND.getExceptionMessage()));

        MatchingPost matchingPost = createMatchingPostDto.of(team, writerInTeam);
        matchingPostRepository.save(matchingPost);

        Member teamOwner = memberRepository.findByMemberId(writerId).orElseThrow(() -> new EntityNotFoundException(MEMBER_NOT_FOUNT.getExceptionMessage()));
        saveMatchingOwner(team, teamOwner, matchingPost);

        if (isExistTeamParticipant(recruiterType, participantsId)){
            List<Member> memberList = memberRepository.findByMemberIdIn(participantsId);
            saveMatchingParticipants(team, memberList, matchingPost);
        }
    }

    private void saveMatchingOwner(Team team, Member member, MatchingPost matchingPost) {
        Matching matchingTeamOwner = Matching.builder()
                        .matchingPost(matchingPost)
                        .member(member)
                        .team(team)
                        .build();

        matchingRepository.save(matchingTeamOwner);
    }

    private void saveMatchingParticipants(Team team, List<Member> memberList, MatchingPost matchingPost) {
        List<Matching> matchingParticipantList = memberList.stream().map(member -> Matching.builder()
                        .matchingPost(matchingPost)
                        .member(member)
                        .team(team)
                        .build())
                .toList();

        matchingRepository.saveAll(matchingParticipantList);
    }

    private boolean isExistTeamParticipant(RecruiterType recruiterType, List<String> participantsPk) {
        return recruiterType.equals(RecruiterType.TEAM) && !participantsPk.isEmpty();
    }

}
