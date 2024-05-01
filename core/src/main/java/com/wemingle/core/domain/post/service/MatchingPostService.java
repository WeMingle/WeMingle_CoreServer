package com.wemingle.core.domain.post.service;

import com.wemingle.core.domain.bookmark.entity.BookmarkedMatchingPost;
import com.wemingle.core.domain.bookmark.repository.BookmarkRepository;
import com.wemingle.core.domain.category.sports.entity.sportstype.SportsType;
import com.wemingle.core.domain.img.service.S3ImgService;
import com.wemingle.core.domain.matching.entity.Matching;
import com.wemingle.core.domain.matching.repository.MatchingRepository;
import com.wemingle.core.domain.member.entity.Member;
import com.wemingle.core.domain.member.repository.MemberRepository;
import com.wemingle.core.domain.post.dto.MatchingPostDto;
import com.wemingle.core.domain.post.dto.MatchingPostMapDto;
import com.wemingle.core.domain.post.dto.sortoption.SortOption;
import com.wemingle.core.domain.post.entity.MatchingPost;
import com.wemingle.core.domain.post.entity.MatchingPostArea;
import com.wemingle.core.domain.post.entity.MatchingPostMatchingDate;
import com.wemingle.core.domain.post.entity.abillity.Ability;
import com.wemingle.core.domain.post.entity.area.AreaName;
import com.wemingle.core.domain.post.entity.gender.Gender;
import com.wemingle.core.domain.post.entity.locationselectiontype.LocationSelectionType;
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
import java.util.*;
import java.util.function.Predicate;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

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

    public Integer getFilteredMatchingPostCnt(RecruitmentType recruitmentType,
                                              Ability ability,
                                              Gender gender,
                                              RecruiterType recruiterType,
                                              List<AreaName> areaList,
                                              LocalDate dateFilter,
                                              YearMonth monthFilter,
                                              Boolean excludeExpired,
                                              SportsType sportsType) {
        isDateFilterAndMonthFilterCoexist(dateFilter, monthFilter);

        return matchingPostRepository.findFilteredMatchingPostCnt(
                recruitmentType,
                ability,
                gender,
                recruiterType,
                areaList,
                excludeExpired == null ? null : LocalDate.now(),
                dateFilter,
                monthFilter,
                sportsType
        );
    }

    public Integer getFilteredMatchingPostByMapCnt(RecruitmentType recruitmentType,
                                                   Ability ability,
                                                   Gender gender,
                                                   RecruiterType recruiterType,
                                                   List<LocalDate> dateFilter,
                                                   YearMonth monthFilter,
                                                   Boolean excludeExpired,
                                                   LocalDate lastExpiredDate,
                                                   SportsType sportsType,
                                                   double topLat,
                                                   double bottomLat,
                                                   double leftLon,
                                                   double rightLon,
                                                   boolean excludeRegionUnit) {

        return matchingPostRepository.findFilteredMatchingPostByMapCnt(
                recruitmentType,
                ability,
                gender,
                recruiterType,
                excludeExpired == null ? null : LocalDate.now(),
                dateFilter,
                monthFilter,
                excludeExpired,
                lastExpiredDate,
                sportsType,
                topLat,
                bottomLat,
                leftLon,
                rightLon,
                excludeRegionUnit
        );
    }

    public LinkedHashMap<String, Object> getFilteredMatchingPostByCalendar(String memberId,
                                                                           Long lastIdx,
                                                                           RecruitmentType recruitmentType,
                                                                           Ability ability,
                                                                           Gender gender,
                                                                           RecruiterType recruiterType,
                                                                           List<AreaName> areaList,
                                                                           LocalDate dateFilter,
                                                                           YearMonth monthFilter,
                                                                           Boolean excludeExpired,
                                                                           SortOption sortOption,
                                                                           LocalDate lastExpiredDate,
                                                                           Integer callCnt,
                                                                           SportsType sportsType){
        isDateFilterAndMonthFilterCoexist(dateFilter, monthFilter);

        List<MatchingPost> filteredMatchingPost;
        filteredMatchingPost = getMatchingPostByCalendar(lastIdx, recruitmentType, ability, gender, recruiterType, areaList, dateFilter, monthFilter, excludeExpired, sortOption, lastExpiredDate, callCnt, sportsType);
        
        Integer nextUrlCallCnt = createNextUrlCallCnt(callCnt, filteredMatchingPost);

        String nextRetrieveUrlParams = createNextRetrieveUrlParamsByCalendar(getLastIdxInMatchingPostList(filteredMatchingPost), recruitmentType, ability, gender, recruiterType, areaList, dateFilter, monthFilter, excludeExpired, sortOption, getLastExpiredDateInMatchingPostList(sortOption, filteredMatchingPost), nextUrlCallCnt);


        LinkedHashMap<String, Object> responseObj = createResponseObj(filteredMatchingPost, nextRetrieveUrlParams);


        List<BookmarkedMatchingPost> bookmarkedByMatchingPosts = bookmarkRepository.findBookmarkedByMatchingPosts(filteredMatchingPost, memberId);

        HashMap<Long, Object> postsMap = createMatchingPostDtoMap(filteredMatchingPost, bookmarkedByMatchingPosts);
        responseObj.put("post list", postsMap);
        return responseObj;
    }

    public LinkedHashMap<String, Object> getFilteredMatchingPostByMap(String memberId,
                                                                      Long lastIdx,
                                                                      RecruitmentType recruitmentType,
                                                                      Ability ability,
                                                                      Gender gender,
                                                                      RecruiterType recruiterType,
                                                                      List<LocalDate> dateFilter,
                                                                      YearMonth monthFilter,
                                                                      Boolean excludeExpired,
                                                                      SortOption sortOption,
                                                                      LocalDate lastExpiredDate,
                                                                      Integer callCnt,
                                                                      SportsType sportsType,
                                                                      double topLat,
                                                                      double bottomLat,
                                                                      double leftLon,
                                                                      double rightLon,
                                                                      boolean excludeRegionUnit) {
        List<MatchingPost> filteredMatchingPost;
        filteredMatchingPost = getMatchingPostByMap(lastIdx, recruitmentType, ability, gender, recruiterType, dateFilter, monthFilter, excludeExpired, sortOption, lastExpiredDate, callCnt, sportsType, topLat, bottomLat, leftLon, rightLon, excludeRegionUnit);

        Integer nextUrlCallCnt = createNextUrlCallCnt(callCnt, filteredMatchingPost);

        String nextRetrieveUrlParams = createNextRetrieveUrlParamsByMap(getLastIdxInMatchingPostList(filteredMatchingPost), recruitmentType, ability, gender, recruiterType, dateFilter, monthFilter, excludeExpired, sortOption, getLastExpiredDateInMatchingPostList(sortOption, filteredMatchingPost), nextUrlCallCnt, topLat, bottomLat, leftLon, rightLon);


        LinkedHashMap<String, Object> responseObj = createResponseObj(filteredMatchingPost, nextRetrieveUrlParams);


        List<BookmarkedMatchingPost> bookmarkedByMatchingPosts = bookmarkRepository.findBookmarkedByMatchingPosts(filteredMatchingPost, memberId);

        HashMap<Long, Object> postsMap = createMatchingPostDtoMap(filteredMatchingPost, bookmarkedByMatchingPosts);
        responseObj.put("post list", postsMap);
        return responseObj;
    }

    private List<MatchingPost> getMatchingPostByMap(Long lastIdx, RecruitmentType recruitmentType, Ability ability, Gender gender, RecruiterType recruiterType, List<LocalDate> dateFilter, YearMonth monthFilter, Boolean excludeExpired, SortOption sortOption, LocalDate lastExpiredDate, Integer callCnt, SportsType sportsType, double topLat, double bottomLat, double leftLon, double rightLon, boolean excludeRegionUnit) {
        List<MatchingPost> filteredMatchingPost;
        switch (Objects.requireNonNull(sortOption)) {

            case NEW -> filteredMatchingPost = matchingPostRepository.findFilteredMatchingPostByMap(
                    lastIdx,
                    recruitmentType,
                    ability,
                    gender,
                    recruiterType,
                    excludeExpired == null ? null : LocalDate.now(),
                    dateFilter,
                    monthFilter,
                    sortOption,
                    lastExpiredDate,
                    sportsType,
                    topLat,
                    bottomLat,
                    leftLon,
                    rightLon,
                    excludeRegionUnit,
                    PageRequest.of(0, 30)
            );
            case DEADLINE -> {
                int pageNumber = Optional.ofNullable(callCnt).orElse(0);
                filteredMatchingPost = matchingPostRepository.findFilteredMatchingPostByMap(
                        null,
                        recruitmentType,
                        ability,
                        gender,
                        recruiterType,
                        excludeExpired == null ? null : LocalDate.now(),
                        dateFilter,
                        monthFilter,
                        sortOption,
                        lastExpiredDate,
                        sportsType,
                        topLat,
                        bottomLat,
                        leftLon,
                        rightLon,
                        excludeRegionUnit,
                        PageRequest.of(pageNumber, 30)
                );
                removeDuplicatePosts(lastIdx, filteredMatchingPost);

                if (filteredMatchingPost.size() < 30) {
                    filteredMatchingPost.addAll(
                            matchingPostRepository.findFilteredMatchingPostByMap(
                                    null,
                                    recruitmentType,
                                    ability,
                                    gender,
                                    recruiterType,
                                    excludeExpired == null ? null : LocalDate.now(),
                                    dateFilter,
                                    monthFilter,
                                    sortOption,
                                    lastExpiredDate,
                                    sportsType,
                                    topLat,
                                    bottomLat,
                                    leftLon,
                                    rightLon,
                                    excludeRegionUnit,
                                    PageRequest.of(pageNumber +1, 30)
                            )
                    );
                }
                filteredMatchingPost = filteredMatchingPost.stream().limit(30).toList();
            }
            default -> throw new RuntimeException("unknown enum value");
        }
        return filteredMatchingPost;
    }

    private List<MatchingPost> getMatchingPostByMapDetail(RecruitmentType recruitmentType, Ability ability, Gender gender, RecruiterType recruiterType, List<LocalDate> dateFilter, YearMonth monthFilter, Boolean excludeExpired, LocalDate lastExpiredDate, SportsType sportsType, double topLat, double bottomLat, double leftLon, double rightLon, boolean excludeRegionUnit) {
        return matchingPostRepository.findFilteredMatchingPostByMapDetail(
                recruitmentType,
                ability,
                gender,
                recruiterType,
                excludeExpired == null ? null : LocalDate.now(),
                dateFilter,
                monthFilter,
                lastExpiredDate,
                sportsType,
                topLat,
                bottomLat,
                leftLon,
                rightLon,
                excludeRegionUnit
        );
    }

    public LinkedHashMap<String, Object> getFilteredMatchingPostByMapDetail(String memberId,
                                                                            RecruitmentType recruitmentType,
                                                                            Ability ability,
                                                                            Gender gender,
                                                                            RecruiterType recruiterType,
                                                                            List<LocalDate> dateFilter,
                                                                            YearMonth monthFilter,
                                                                            Boolean excludeExpired,
                                                                            LocalDate lastExpiredDate,
                                                                            SportsType sportsType,
                                                                            double topLat,
                                                                            double bottomLat,
                                                                            double leftLon,
                                                                            double rightLon,
                                                                            boolean excludeRegionUnit) {
        List<MatchingPost> filteredMatchingPost;
        filteredMatchingPost = getMatchingPostByMapDetail(recruitmentType, ability, gender, recruiterType, dateFilter, monthFilter, excludeExpired, lastExpiredDate, sportsType, topLat, bottomLat, leftLon, rightLon, excludeRegionUnit);

        LinkedHashMap<String, Object> responseObj = createResponseObj(filteredMatchingPost, null);

        List<BookmarkedMatchingPost> bookmarkedByMatchingPosts = bookmarkRepository.findBookmarkedByMatchingPosts(filteredMatchingPost, memberId);
        List<Matching> matchingResultByMemberId = matchingRepository.findByMatchingResultByMemberId(memberId, filteredMatchingPost);

        HashMap<Long, Object> postsMap = createMatchingPostDtoMapDetail(filteredMatchingPost, bookmarkedByMatchingPosts, matchingResultByMemberId);
        responseObj.put("post list", postsMap);
        return responseObj;
    }

    private LinkedHashMap<String, Object> createResponseObj(List<MatchingPost> filteredMatchingPost, String nextRetrieveUrlParams) {
        LinkedHashMap<String, Object> responseObj = new LinkedHashMap<>();
        if (filteredMatchingPost.isEmpty()) {
            responseObj.put("next url", null);
        } else {
            responseObj.put("next url", serverIp + "/post/mathch/calendar?"+ nextRetrieveUrlParams);
        }
        return responseObj;
    }

    private LocalDate getLastExpiredDateInMatchingPostList(SortOption sortOption, List<MatchingPost> filteredMatchingPost) {

        return filteredMatchingPost.isEmpty()||!sortOption.equals(SortOption.DEADLINE) ? null : filteredMatchingPost.get(filteredMatchingPost.size() - 1).getExpiryDate();
    }

    private Long getLastIdxInMatchingPostList(List<MatchingPost> filteredMatchingPost) {
        return filteredMatchingPost.isEmpty() ? null : filteredMatchingPost.get(filteredMatchingPost.size() - 1).getPk();
    }

    private HashMap<Long, Object> createMatchingPostDtoMap(List<MatchingPost> filteredMatchingPost, List<BookmarkedMatchingPost> bookmarkedByMatchingPosts) {
        HashMap<Long, Object> postsMap = new LinkedHashMap<>();

        filteredMatchingPost.forEach(post -> {
                    boolean isBookmarked = bookmarkedByMatchingPosts.stream().anyMatch(bookmark -> bookmark.getMatchingPost().equals(post));

                    postsMap.put(post.getPk(), MatchingPostDto.ResponseMatchingPostDto.builder()
                            .writer(post.getWriter().getTeam().getTeamName())
                            .matchingDate(getMatchingDates(post))
                            .areaList(post.getAreaList().stream().map(MatchingPostArea::getAreaName).toList())
                            .ability(post.getAbility())
                            .isLocationConsensusPossible(post.isLocationConsensusPossible())
                            .contents(post.getContent())
                            .recruiterType(post.getRecruiterType())
                            .profilePicUrl(post.getRecruiterType().equals(RecruiterType.TEAM) ? s3ImgService.getGroupProfilePicUrl(post.getTeam().getProfileImgId()) : s3ImgService.getMemberProfilePicUrl(post.getTeam().getProfileImgId()))
                            .matchingCnt(post.getTeam().getCompletedMatchingCnt())
                            .isBookmarked(isBookmarked)
                            .viewCnt(post.getViewCnt())
                            .expiryDate(post.getExpiryDate())
                            .build());
                }
        );
        return postsMap;
    }

    private List<LocalDate> getMatchingDates(MatchingPost matchingPost){
        return matchingPost.getMatchingDates().stream().map(MatchingPostMatchingDate::getMatchingDate).toList();
    }

    private HashMap<Long, Object> createMatchingPostDtoMapDetail(List<MatchingPost> filteredMatchingPost, List<BookmarkedMatchingPost> bookmarkedByMatchingPosts, List<Matching> matchingResultByMemberId) {
        HashMap<Long, Object> postsMap = new LinkedHashMap<>();

        filteredMatchingPost.forEach(post -> {
            boolean isBookmarked = bookmarkedByMatchingPosts.stream().anyMatch(bookmark -> bookmark.getMatchingPost().equals(post));
            boolean isMatchedBefore = matchingResultByMemberId.stream().anyMatch(matching -> matching.getMatchingPost().equals(post));

            postsMap.put(post.getPk(), MatchingPostDto.ResponseMatchingPostByMapDetailDto.builder()
                    .writer(post.getWriter().getTeam().getTeamName())
                    .matchingDate(getMatchingDates(post))
                    .areaList(post.getAreaList().stream().map(MatchingPostArea::getAreaName).toList())
                    .ability(post.getAbility())
                    .isLocationConsensusPossible(post.isLocationConsensusPossible())
                    .contents(post.getContent())
                    .recruiterType(post.getRecruiterType())
                    .profilePicUrl(post.getRecruiterType().equals(RecruiterType.TEAM) ? s3ImgService.getGroupProfilePicUrl(post.getTeam().getProfileImgId()) : s3ImgService.getMemberProfilePicUrl(post.getTeam().getProfileImgId()))
                    .matchingCnt(post.getTeam().getCompletedMatchingCnt())
                    .isBookmarked(isBookmarked)
                    .viewCnt(post.getViewCnt())
                    .expiryDate(post.getExpiryDate())
                    .lat(post.getLat())
                    .lon(post.getLon())
                    .isMatchedBefore(isMatchedBefore)
                    .build());
                }
        );
        return postsMap;
    }

    private List<MatchingPost> getMatchingPostByCalendar(Long lastIdx, RecruitmentType recruitmentType, Ability ability, Gender gender, RecruiterType recruiterType, List<AreaName> areaList, LocalDate dateFilter, YearMonth monthFilter, Boolean excludeExpired, SortOption sortOption, LocalDate lastExpiredDate, Integer callCnt, SportsType sportsType) {
        List<MatchingPost> filteredMatchingPost;
        switch (Objects.requireNonNull(sortOption)) {

            case NEW -> filteredMatchingPost = matchingPostRepository.findFilteredMatchingPostByCalendar(
                    lastIdx,
                    recruitmentType,
                    ability,
                    gender,
                    recruiterType,
                    areaList,
                    excludeExpired == null ? null : LocalDate.now(),
                    dateFilter,
                    monthFilter,
                    sortOption,
                    null,
                    sportsType,
                    PageRequest.of(0, 30)
            );
            case DEADLINE -> {
                int pageNumber = Optional.ofNullable(callCnt).orElse(0);
                filteredMatchingPost = matchingPostRepository.findFilteredMatchingPostByCalendar(
                    null,
                    recruitmentType,
                    ability,
                    gender,
                    recruiterType,
                    areaList,
                    excludeExpired == null ? null : LocalDate.now(),
                    dateFilter,
                    monthFilter,
                    sortOption,
                    lastExpiredDate,
                    sportsType,
                    PageRequest.of(pageNumber, 30)
                );
                removeDuplicatePosts(lastIdx, filteredMatchingPost);

                if (filteredMatchingPost.size() < 30) {
                    filteredMatchingPost.addAll(
                            matchingPostRepository.findFilteredMatchingPostByCalendar(
                                    null,
                                    recruitmentType,
                                    ability,
                                    gender,
                                    recruiterType,
                                    areaList,
                                    excludeExpired == null ? null : LocalDate.now(),
                                    dateFilter,
                                    monthFilter,
                                    sortOption,
                                    lastExpiredDate,
                                    sportsType,
                                    PageRequest.of(pageNumber +1, 30)
                            )
                    );
                }
                filteredMatchingPost = filteredMatchingPost.stream().limit(30).toList();
            }
            default -> throw new RuntimeException("unknown enum value");
        }
        return filteredMatchingPost;
    }

    private Integer createNextUrlCallCnt(Integer callCnt, List<MatchingPost> filteredMatchingPost) {

        Predicate<LocalDate> sameExpiredDate = localDate ->
                filteredMatchingPost.stream()
                        .allMatch(p -> p.getExpiryDate().equals(localDate));

        Optional<MatchingPost> firstPost = filteredMatchingPost.stream().findFirst();

        boolean isSameViewCountOrExpiredDate = firstPost.map(matchingPost -> sameExpiredDate.test(matchingPost.getExpiryDate()))
                .orElse(false);

        if (isSameViewCountOrExpiredDate) {
            return callCnt == null ? 1 : callCnt + 1;
        }
        return null;
    }

    private String createNextRetrieveUrlParamsByCalendar(Long lastIdx, RecruitmentType recruitmentType, Ability ability, Gender gender, RecruiterType recruiterType, List<AreaName> areaList, LocalDate dateFilter, YearMonth monthFilter, Boolean excludeExpired, SortOption sortOption, LocalDate lastExpiredDate, Integer callCnt) {
        Map<String, Object> parameters = new LinkedHashMap<>();
        parameters.put("lastIdx", lastIdx);
        parameters.put("recruitmentType", recruitmentType);
        parameters.put("ability", ability);
        parameters.put("gender", gender);
        parameters.put("recruiterType", recruiterType);
        parameters.put("areaList", areaList != null && !areaList.isEmpty() ? areaList : null);
        parameters.put("dateFilter", dateFilter);
        parameters.put("monthFilter", monthFilter);
        parameters.put("excludeExpired", excludeExpired);
        parameters.put("sortOption", sortOption);
        parameters.put("lastExpireDate", lastExpiredDate);
        if (sortOption.equals(SortOption.DEADLINE)) {
            parameters.put("callCnt", callCnt);
        }


        return parameters.entrySet().stream()
                .filter(entry -> entry.getValue() != null)
                .map(entry -> entry.getKey() + "=" + entry.getValue())
                .collect(Collectors.joining("&"));
    }

    private String createNextRetrieveUrlParamsByMap(Long lastIdx, RecruitmentType recruitmentType, Ability ability, Gender gender, RecruiterType recruiterType, List<LocalDate> dateFilter, YearMonth monthFilter, Boolean excludeExpired, SortOption sortOption, LocalDate lastExpiredDate, Integer callCnt, double topLat, double bottomLat, double leftLon, double rightLon) {
        Map<String, Object> parameters = new LinkedHashMap<>();
        parameters.put("lastIdx", lastIdx);
        parameters.put("recruitmentType", recruitmentType);
        parameters.put("ability", ability);
        parameters.put("gender", gender);
        parameters.put("recruiterType", recruiterType);
        parameters.put("dateFilter", dateFilter);
        parameters.put("monthFilter", monthFilter);
        parameters.put("excludeExpired", excludeExpired);
        parameters.put("sortOption", sortOption);
        parameters.put("lastExpireDate", lastExpiredDate);
        parameters.put("topLat", topLat);
        parameters.put("bottomLat", bottomLat);
        parameters.put("leftLon", leftLon);
        parameters.put("rightLon", rightLon);

        if (sortOption.equals(SortOption.DEADLINE)) {
            parameters.put("callCnt", callCnt);
        }


        return parameters.entrySet().stream()
                .filter(entry -> entry.getValue() != null)
                .map(entry -> entry.getKey() + "=" + entry.getValue())
                .collect(Collectors.joining("&"));
    }

    private void removeDuplicatePosts(Long lastIdx, List<MatchingPost> filteredMatchingPost) {
        Iterator<MatchingPost> iterator = filteredMatchingPost.iterator();
        log.info("{}",iterator.hasNext());
        log.info("{}",!Objects.isNull(lastIdx));
        log.info("{}",filteredMatchingPost.stream().anyMatch(matchingPost -> matchingPost.getPk().equals(lastIdx)));
        while (iterator.hasNext()&&!Objects.isNull(lastIdx)&& filteredMatchingPost.stream().anyMatch(matchingPost -> matchingPost.getPk().equals(lastIdx))) {
            MatchingPost next = iterator.next();
            iterator.remove();
            log.info("delete this = {}",next.getPk());
            if (next.getPk().equals(lastIdx)) {
                break;
            }
        }
    }

    private void isDateFilterAndMonthFilterCoexist(LocalDate dateFilter, YearMonth monthFilter) {
        if (dateFilter != null && monthFilter != null) {
            throw new RuntimeException(DATE_MONTH_CANT_COEXIST.getExceptionMessage());
        }
        if (dateFilter == null && monthFilter == null) {
            throw new RuntimeException(DATE_OR_MONTH_MUST_EXIST.getExceptionMessage());
        }
    }

    public LinkedHashMap<Long, MatchingPostDto.ResponseCompletedMatchingPost> getCompletedMatchingPosts(Long nextIdx, RecruiterType recruiterType, boolean excludeCompleteMatchesFilter, String memberId){
        Member member = memberRepository.findByMemberId(memberId).orElseThrow(() -> new EntityNotFoundException(MEMBER_NOT_FOUNT.getExceptionMessage()));
        List<MatchingPost> matchingPostWrittenReview = teamReviewRepository.findMatchingPostWithMemberId(member);
        List<MatchingPost> matchingPosts = matchingPostRepository.findCompletedMatchingPosts(nextIdx, recruiterType, excludeCompleteMatchesFilter, member, matchingPostWrittenReview);
//        List<MatchingPostArea> matchingPostAreas = matchingPostAreaRepository.findByMatchingPostIn(matchingPosts);
        List<TeamMember> managerOrHigherTeamMembers = teamMemberRepository.findWithManagerOrHigher(getTeamsWithMatchingPosts(matchingPosts));
        LinkedHashMap<Long, MatchingPostDto.ResponseCompletedMatchingPost> responseHashMap = new LinkedHashMap<>();

        matchingPosts.forEach(matchingPost -> responseHashMap.put(matchingPost.getPk(), MatchingPostDto.ResponseCompletedMatchingPost.builder()
                .matchingDate(getMatchingDates(matchingPost))
                .recruiterType(matchingPost.getRecruiterType())
                .teamName(matchingPost.getTeam().getTeamName())
                .completedMatchingCnt(matchingPost.getTeam().getCompletedMatchingCnt())
                .content(matchingPost.getContent())
//                .areaNames(getAreaNames(matchingPost, matchingPostAreas))
                .areaNames(getAreas(matchingPost))
                .isLocationConsensusPossible(matchingPost.isLocationConsensusPossible())
                .ability(matchingPost.getAbility())
                .profileImgUrl(getProfileImgUrl(matchingPost))
                .matchingStatus(matchingStatusFactory(matchingPost.getMatchingStatus(), getMinMatchingDate(matchingPost)))
                .scheduledRequestDescription(getScheduledRequest(matchingPost, managerOrHigherTeamMembers, member, matchingPostWrittenReview))
                .build()));

        return responseHashMap;
    }

    private List<Team> getTeamsWithMatchingPosts(List<MatchingPost> matchingPosts) {
        return matchingPosts.stream().map(MatchingPost::getTeam).toList();
    }

//
//    private List<AreaName> getAreaNames(MatchingPost matchingPost, List<MatchingPostArea> matchingPostAreas) {
//        return matchingPostAreas.stream()
//                .filter(matchingPostArea -> matchingPostArea.getMatchingPost().equals(matchingPost))
//                .map(MatchingPostArea::getAreaName)
//                .toList();
//    }

    private String getProfileImgUrl(MatchingPost matchingPost) {
        return isTeam(matchingPost)
                ? s3ImgService.getGroupProfilePicUrl(matchingPost.getTeam().getProfileImgId())
                : s3ImgService.getMemberProfilePicUrl(matchingPost.getTeam().getProfileImgId());
    }

    private boolean isTeam(MatchingPost matchingPost) {
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

    private String getScheduledRequest(MatchingPost matchingPost, List<TeamMember> managerOrHigherTeamMembers, Member member, List<MatchingPost> matchingPostWrittenReview) {
        return isTeamOwner(matchingPost, managerOrHigherTeamMembers, member)
                ? createScheduledRequestWithOwner(matchingPost.getMatchingStatus(), getMinMatchingDate(matchingPost), matchingPost, matchingPostWrittenReview)
                : createScheduledRequestWithMember(matchingPost.getMatchingStatus(), matchingPost, matchingPostWrittenReview);
    }

    private static LocalDate getMinMatchingDate(MatchingPost matchingPost) {
        return matchingPost.getMatchingDates().stream()
                .map(MatchingPostMatchingDate::getMatchingDate)
                .min(Comparator.naturalOrder())
                .orElseThrow(() -> new RuntimeException(MATCHING_DATE_NOT_FOUND.getExceptionMessage()));
    }

    private boolean isTeamOwner(MatchingPost matchingPost, List<TeamMember> managerOrHigherTeamMembers, Member member) {
        List<Member> teamMembersInPost = managerOrHigherTeamMembers.stream()
                .filter(teamMember -> matchingPost.getTeam().equals(teamMember.getTeam()))
                .map(TeamMember::getMember)
                .toList();

        return teamMembersInPost.contains(member);
    }

    private String createScheduledRequestWithOwner(MatchingStatus matchingStatus,
                                                   LocalDate matchingDate,
                                                   MatchingPost matchingPost,
                                                   List<MatchingPost> matchingPostWrittenReview) {
        switch (matchingStatus) {
            case CANCEL -> {
                return RENEW_MATCHING_POST.getDescription();
            }
            case COMPLETE -> {
                long remainDays = Duration.between(LocalDate.now().atStartOfDay(), matchingDate.atStartOfDay()).toDays();

                if (remainDays >= PERMIT_CANCEL_DURATION) {
                    return CANCEL_BY_CHAT.getDescription();
                } else if (remainDays >= 0) {
                    return CANCEL_NOT_PERMITTED_DURATION.getDescription();
                } else {
                    return checkReviewWrittenPost(matchingPost, matchingPostWrittenReview);
                }
            }
            default -> throw new RuntimeException(INVALID_MATCHING_POST_STATUS.getExceptionMessage());
        }
    }

    private String checkReviewWrittenPost(MatchingPost matchingPost, List<MatchingPost> matchingPostWithMemberId) {
        if (matchingPostWithMemberId.contains(matchingPost)) {
            return AFTER_WRITE_REVIEW.getDescription();
        } else {
            return BEFORE_WRITE_REVIEW.getDescription();
        }
    }

    private String createScheduledRequestWithMember(MatchingStatus matchingStatus,
                                                    MatchingPost matchingPost,
                                                    List<MatchingPost> matchingPostWrittenReview) {
        if (matchingStatus.equals(MatchingStatus.COMPLETE)) {
            return checkReviewWrittenPost(matchingPost, matchingPostWrittenReview);
        } else {
            return NO_PERMISSION.getDescription();
        }
    }

    @Transactional
    public void saveMatchingPost(MatchingPostDto.CreateMatchingPostDto createMatchingPostDto, String writerId){
        RecruiterType recruiterType = createMatchingPostDto.getRecruiterType();
        Long teamPk = createMatchingPostDto.getTeamPk();
        List<String> participantsId = createMatchingPostDto.getParticipantsId();

        Team team = teamRepository.findById(teamPk).orElseThrow(() -> new EntityNotFoundException(TEAM_NOT_FOUND.getExceptionMessage()));
        TeamMember writerInTeam = teamMemberRepository.findByTeamAndMember_MemberId(team, writerId)
                .orElseThrow(() -> new EntityNotFoundException(TEAM_MEMBER_NOT_FOUND.getExceptionMessage()));

        MatchingPost matchingPost = createMatchingPostDto.of(team, writerInTeam, createMatchingPostDto.getMatchingDate());
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

    public List<MatchingPostMapDto> getMatchingPostByMap(double topLat,
                                     double bottomLat,
                                     double leftLon,
                                     double rightLon,
                                     int heightTileCnt,
                                     int widthTileCnt) {
        List<MatchingPost> matchingPostInMap = matchingPostRepository.findMatchingPostInMap(topLat, bottomLat, leftLon, rightLon);

        double latTileRange = (topLat - bottomLat) / heightTileCnt;
        double lonTileRange = (rightLon - leftLon) / widthTileCnt;

        List<MatchingPostMapDto> clusterData = new LinkedList<>();

        IntStream.rangeClosed(1,heightTileCnt)
                .forEach(hIdx->
                    IntStream.rangeClosed(1,widthTileCnt)
                            .forEach(wIdx->{
                                List<MatchingPost> matchingPosts = matchingPostInMap.stream()
                                        .filter(matchingPost ->
                                                matchingPost.getLat() <= (topLat - latTileRange * (wIdx - 1)) &&
                                                        matchingPost.getLat() >= (topLat - latTileRange * wIdx) &&
                                                        matchingPost.getLon() >= (leftLon + lonTileRange * (hIdx - 1)) &&
                                                        matchingPost.getLon() <= (leftLon + lonTileRange * hIdx)
                                        ).toList();
                                        if (!matchingPosts.isEmpty()) {
                                            clusterData.add(MatchingPostMapDto.builder()
                                                    .lat(matchingPosts.get(0).getLat())
                                                    .lon(matchingPosts.get(0).getLon())
                                                    .cnt(matchingPosts.size()).build());
                                        }
                                }
                            )
                );
        return clusterData;
    }

    @Transactional
    public void rePostMatchingPost(MatchingPost matchingPost){
        matchingPost.updateForRePost();
    }


    @Transactional
    public void completeMatchingPost(Long matchingPostPk){
        MatchingPost matchingPost = matchingPostRepository.findById(matchingPostPk)
                .orElseThrow(() -> new EntityNotFoundException(ExceptionMessage.MATCHING_POST_NOT_FOUND.getExceptionMessage()));

        matchingPost.complete();
    }

    public HashMap<Long, MatchingPostDto.ResponseTop15PopularPost> getTop15PopularPost(){
        List<MatchingPost> top15PopularPost = matchingPostRepository.findTop15PopularPost();
        LinkedHashMap<Long, MatchingPostDto.ResponseTop15PopularPost> responseData = new LinkedHashMap<>();

        top15PopularPost.forEach(matchingPost -> responseData.put(matchingPost.getPk(), MatchingPostDto.ResponseTop15PopularPost.builder()
                .imgUrl(getProfileImgUrl(matchingPost))
                .writer(matchingPost.getWriter().getNickname())
                .areas(getAreas(matchingPost))
                .matchingCnt(matchingPost.getTeam().getCompletedMatchingCnt())
                .matchingDate(getMatchingDates(matchingPost))
                .expiryDate(matchingPost.getExpiryDate())
                .recruiterType(matchingPost.getRecruiterType())
                .ability(matchingPost.getAbility())
                .isLocationConsensusPossible(matchingPost.isLocationConsensusPossible())
                .build()));

        return responseData;
    }

    private List<String> getAreas(MatchingPost matchingPost){
        return matchingPost.getLocationSelectionType().equals(LocationSelectionType.SEARCH_BASED)
                ? List.of(matchingPost.getLocationName())
                : matchingPost.getAreaList().stream().map(matchingPostArea -> matchingPostArea.getAreaName().toString()).toList();
    }

    public HashMap<Long, MatchingPostDto.ResponseTop200PopularPost> getTop200PopularPost(String memberId){
        List<MatchingPost> top200PopularPost = matchingPostRepository.findTop200PopularPost();
        LinkedHashMap<Long, MatchingPostDto.ResponseTop200PopularPost> responseData = new LinkedHashMap<>();
        List<BookmarkedMatchingPost> bookmarkedMatchingPosts = bookmarkRepository.findBookmarkedByMatchingPosts(top200PopularPost, memberId);

        top200PopularPost.forEach(matchingPost ->
            responseData.put(matchingPost.getPk(), MatchingPostDto.ResponseTop200PopularPost.builder()
                .imgUrl(getProfileImgUrl(matchingPost))
                .writer(matchingPost.getWriter().getNickname())
                        .content(matchingPost.getContent())
                .areas(getAreas(matchingPost))
                .matchingCnt(matchingPost.getTeam().getCompletedMatchingCnt())
                .matchingDate(getMatchingDates(matchingPost))
                .recruiterType(matchingPost.getRecruiterType())
                .ability(matchingPost.getAbility())
                .isLocationConsensusPossible(matchingPost.isLocationConsensusPossible())
                        .isBookmarked(isBookmarked(matchingPost, bookmarkedMatchingPosts))
                        .isExpired(isExpired(matchingPost))
                .build()));

        return responseData;
    }

    private boolean isBookmarked(MatchingPost matchingPost, List<BookmarkedMatchingPost> bookmarkedMatchingPosts) {
        return bookmarkedMatchingPosts.stream().map(BookmarkedMatchingPost::getMatchingPost).anyMatch(matchingPost::equals);
    }

    protected boolean isExpired(MatchingPost matchingPost) {
        return matchingPost.getMatchingStatus().equals(MatchingStatus.COMPLETE) || matchingPost.getExpiryDate().isBefore(LocalDate.now());
    }

    public HashMap<Long, MatchingPostDto.ResponseRecentPost> getRecentPost(Long nextIdx, String memberId){
        List<MatchingPost> recentPost = matchingPostRepository.findRecentMatchingPost(nextIdx);
        LinkedHashMap<Long, MatchingPostDto.ResponseRecentPost> responseData = new LinkedHashMap<>();
        List<BookmarkedMatchingPost> bookmarkedMatchingPosts = bookmarkRepository.findBookmarkedByMatchingPosts(recentPost, memberId);

        recentPost.forEach(matchingPost ->
                responseData.put(matchingPost.getPk(), MatchingPostDto.ResponseRecentPost.builder()
                        .imgUrl(getProfileImgUrl(matchingPost))
                        .writer(matchingPost.getWriter().getNickname())
                        .content(matchingPost.getContent())
                        .areas(getAreas(matchingPost))
                        .matchingCnt(matchingPost.getTeam().getCompletedMatchingCnt())
                        .matchingDate(getMatchingDates(matchingPost))
                        .recruiterType(matchingPost.getRecruiterType())
                        .ability(matchingPost.getAbility())
                        .isLocationConsensusPossible(matchingPost.isLocationConsensusPossible())
                        .isBookmarked(isBookmarked(matchingPost, bookmarkedMatchingPosts))
                        .isExpired(isExpired(matchingPost))
                        .build()));

        return responseData;
    }

    public int getSearchPostCnt(String query){
        return matchingPostRepository.findSearchMatchingPostCnt(query);
    }
}
