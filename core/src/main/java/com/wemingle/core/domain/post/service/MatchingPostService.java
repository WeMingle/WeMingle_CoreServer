package com.wemingle.core.domain.post.service;

import com.wemingle.core.domain.bookmark.entity.BookmarkedMatchingPost;
import com.wemingle.core.domain.bookmark.repository.BookmarkMatchingPostRepository;
import com.wemingle.core.domain.category.sports.entity.sportstype.SportsType;
import com.wemingle.core.domain.img.service.S3ImgService;
import com.wemingle.core.domain.matching.entity.Matching;
import com.wemingle.core.domain.matching.entity.MatchingRequest;
import com.wemingle.core.domain.matching.repository.MatchingRepository;
import com.wemingle.core.domain.matching.repository.MatchingRequestRepository;
import com.wemingle.core.domain.member.entity.Member;
import com.wemingle.core.domain.member.service.MemberService;
import com.wemingle.core.domain.post.dto.MatchingPostDto;
import com.wemingle.core.domain.post.dto.MatchingPostMapDto;
import com.wemingle.core.domain.post.dto.sortoption.SortOption;
import com.wemingle.core.domain.post.entity.MatchingPost;
import com.wemingle.core.domain.post.entity.MatchingPostArea;
import com.wemingle.core.domain.post.entity.abillity.Ability;
import com.wemingle.core.domain.post.entity.area.AreaName;
import com.wemingle.core.domain.post.entity.gender.Gender;
import com.wemingle.core.domain.post.entity.locationselectiontype.LocationSelectionType;
import com.wemingle.core.domain.post.entity.matchingstatus.MatchingStatus;
import com.wemingle.core.domain.post.entity.recruitertype.RecruiterType;
import com.wemingle.core.domain.post.repository.MatchingPostAreaRepository;
import com.wemingle.core.domain.post.repository.MatchingPostRepository;
import com.wemingle.core.domain.post.vo.MatchingPostByCalendarVo;
import com.wemingle.core.domain.rating.repository.TeamRatingRepository;
import com.wemingle.core.domain.review.repository.TeamReviewRepository;
import com.wemingle.core.domain.team.entity.Team;
import com.wemingle.core.domain.team.entity.TeamMember;
import com.wemingle.core.domain.team.entity.recruitmenttype.RecruitmentType;
import com.wemingle.core.domain.team.entity.teamtype.TeamType;
import com.wemingle.core.domain.team.repository.TeamMemberRepository;
import com.wemingle.core.domain.team.repository.TeamRepository;
import com.wemingle.core.domain.team.service.TeamMemberService;
import com.wemingle.core.global.exception.NotWriterException;
import com.wemingle.core.global.exceptionmessage.ExceptionMessage;
import com.wemingle.core.global.util.teamrating.TeamRatingUtil;
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
    private final MatchingRepository matchingRepository;
    private final BookmarkMatchingPostRepository bookmarkMatchingPostRepository;
    private final TeamReviewRepository teamReviewRepository;
    private final S3ImgService s3ImgService;
    private final MatchingRequestRepository matchingRequestRepository;
    private final TeamRatingRepository teamRatingRepository;
    private final TeamMemberService teamMemberService;
    private final MemberService memberService;

    @Value("${wemingle.ip}")
    private String serverIp;

    private static final int PERMIT_CANCEL_DURATION = 3;
    private static final int PAGE_SIZE = 30;
    private static final String SEARCH_MATCHING_POST_PATH = "/post/match/result";

    public HashMap<Long, Object> getAllMyPosts(Long nextIdx, RecruiterType recruiterType, String memberId) {
        List<MatchingPost> myAllMatchingPosts = matchingPostRepository.findMyAllMatchingPosts(nextIdx, recruiterType, memberId);
        List<BookmarkedMatchingPost> bookmarkedByMatchingPosts = bookmarkMatchingPostRepository.findBookmarkedByMatchingPosts(myAllMatchingPosts, memberId);
        return createMatchingPostDtoMap(myAllMatchingPosts, bookmarkedByMatchingPosts);

    }

    public MatchingPost getMatchingPostByPostId(Long postId) {
        return matchingPostRepository.findById(postId)
                .orElseThrow(()->new NoSuchElementException(ExceptionMessage.POST_NOT_FOUND.getExceptionMessage()));
    }

    public Integer getFilteredMatchingPostCnt(MatchingPostDto.RequestCalenderCntDto requestCalenderCntDto) {
        isDateFilterAndMonthFilterCoexist(requestCalenderCntDto.getDateFilter(), requestCalenderCntDto.getMonthFilter());

        return matchingPostRepository.findFilteredMatchingPostCnt(
                requestCalenderCntDto.getRecruitmentType(),
                requestCalenderCntDto.getAbility(),
                requestCalenderCntDto.getGender(),
                requestCalenderCntDto.getRecruiterType(),
                requestCalenderCntDto.getAreaList(),
                requestCalenderCntDto.getExcludeExpired() == null ? null : LocalDate.now(),
                requestCalenderCntDto.getDateFilter(),
                requestCalenderCntDto.getMonthFilter(),
                requestCalenderCntDto.getSportsTyp()
        );
    }

    public Integer getFilteredMatchingPostByMapCnt(MatchingPostMapDto.RequestMapCnt requestMapCnt) {

        return matchingPostRepository.findFilteredMatchingPostByMapCnt(
                requestMapCnt.getRecruitmentType(),
                requestMapCnt.getAbility(),
                requestMapCnt.getGender(),
                requestMapCnt.getRecruiterType(),
                requestMapCnt.getExcludeExpired() == null ? null : LocalDate.now(),
                requestMapCnt.getDateFilter(),
                requestMapCnt.getMonthFilter(),
                requestMapCnt.getExcludeExpired(),
                requestMapCnt.getLastExpiredDate(),
                requestMapCnt.getSportsTyp(),
                requestMapCnt.getTopLat(),
                requestMapCnt.getBottomLat(),
                requestMapCnt.getLeftLon(),
                requestMapCnt.getRightLon(),
                requestMapCnt.isExcludeRegionUnit()
        );
    }

    public LinkedHashMap<String, Object> getFilteredMatchingPostByCalendar(String memberId,MatchingPostDto.RequestCalendarDto requestCalendarDto){
        isDateFilterAndMonthFilterCoexist(requestCalendarDto.getDateFilter(), requestCalendarDto.getMonthFilter());

        List<MatchingPost> filteredMatchingPost;
        filteredMatchingPost = getMatchingPostByCalendar(new MatchingPostByCalendarVo(requestCalendarDto));
        
        Integer nextUrlCallCnt = createNextUrlCallCnt(requestCalendarDto.getCallCnt(), filteredMatchingPost);

        String nextRetrieveUrlParams = createNextRetrieveUrlParamsByCalendar(getLastIdxInMatchingPostList(filteredMatchingPost), requestCalendarDto.getRecruitmentType(), requestCalendarDto.getAbility(), requestCalendarDto.getGender(), requestCalendarDto.getRecruiterType(), requestCalendarDto.getAreaList(), requestCalendarDto.getDateFilter(), requestCalendarDto.getMonthFilter(), requestCalendarDto.getExcludeExpired(), requestCalendarDto.getSortOption(), getLastExpiredDateInMatchingPostList(requestCalendarDto.getSortOption(), filteredMatchingPost), nextUrlCallCnt);


        LinkedHashMap<String, Object> responseObj = createResponseObj(filteredMatchingPost, nextRetrieveUrlParams);


        List<BookmarkedMatchingPost> bookmarkedByMatchingPosts = bookmarkMatchingPostRepository.findBookmarkedByMatchingPosts(filteredMatchingPost, memberId);

        HashMap<Long, Object> postsMap = createMatchingPostDtoMap(filteredMatchingPost, bookmarkedByMatchingPosts);
        responseObj.put("post list", postsMap);
        return responseObj;
    }

    public LinkedHashMap<String, Object> getFilteredMatchingPostByMap(String memberId, MatchingPostMapDto.RequestMap requestMap) {
        List<MatchingPost> filteredMatchingPost;
        filteredMatchingPost = getMatchingPostByMap(requestMap);

        Integer nextUrlCallCnt = createNextUrlCallCnt(requestMap.getCallCnt(), filteredMatchingPost);

        String nextRetrieveUrlParams = createNextRetrieveUrlParamsByMap(getLastIdxInMatchingPostList(filteredMatchingPost), requestMap.getRecruitmentType(), requestMap.getAbility(), requestMap.getGender(), requestMap.getRecruiterType(), requestMap.getDateFilter(), requestMap.getMonthFilter(), requestMap.getExcludeExpired(), requestMap.getSortOption(), getLastExpiredDateInMatchingPostList(requestMap.getSortOption(), filteredMatchingPost), nextUrlCallCnt, requestMap.getTopLat(), requestMap.getBottomLat(), requestMap.getLeftLon(), requestMap.getRightLon());


        LinkedHashMap<String, Object> responseObj = createResponseObj(filteredMatchingPost, nextRetrieveUrlParams);


        List<BookmarkedMatchingPost> bookmarkedByMatchingPosts = bookmarkMatchingPostRepository.findBookmarkedByMatchingPosts(filteredMatchingPost, memberId);

        HashMap<Long, Object> postsMap = createMatchingPostDtoMap(filteredMatchingPost, bookmarkedByMatchingPosts);
        responseObj.put("post list", postsMap);
        return responseObj;
    }

    private List<MatchingPost> getMatchingPostByMap(MatchingPostMapDto.RequestMap requestMap) {
        List<MatchingPost> filteredMatchingPost;
        switch (Objects.requireNonNull(requestMap.getSortOption())) {

            case NEW -> filteredMatchingPost = matchingPostRepository.findFilteredMatchingPostByMap(
                    requestMap.getLastIdx(),
                    requestMap.getRecruitmentType(),
                    requestMap.getAbility(),
                    requestMap.getGender(),
                    requestMap.getRecruiterType(),
                    requestMap.getExcludeExpired() == null ? null : LocalDate.now(),
                    requestMap.getDateFilter(),
                    requestMap.getMonthFilter(),
                    requestMap.getSortOption(),
                    requestMap.getLastExpiredDate(),
                    requestMap.getSportsType(),
                    requestMap.getTopLat(),
                    requestMap.getBottomLat(),
                    requestMap.getLeftLon(),
                    requestMap.getRightLon(),
                    requestMap.isExcludeRegionUnit(),
                    PageRequest.of(0, 30)
            );
            case DEADLINE -> {
                int pageNumber = Optional.ofNullable(requestMap.getCallCnt()).orElse(0);
                filteredMatchingPost = matchingPostRepository.findFilteredMatchingPostByMap(
                        null,
                        requestMap.getRecruitmentType(),
                        requestMap.getAbility(),
                        requestMap.getGender(),
                        requestMap.getRecruiterType(),
                        requestMap.getExcludeExpired() == null ? null : LocalDate.now(),
                        requestMap.getDateFilter(),
                        requestMap.getMonthFilter(),
                        requestMap.getSortOption(),
                        requestMap.getLastExpiredDate(),
                        requestMap.getSportsType(),
                        requestMap.getTopLat(),
                        requestMap.getBottomLat(),
                        requestMap.getLeftLon(),
                        requestMap.getRightLon(),
                        requestMap.isExcludeRegionUnit(),
                        PageRequest.of(pageNumber, 30)
                );
                removeDuplicatePosts(requestMap.getLastIdx(), filteredMatchingPost);

                if (filteredMatchingPost.size() < 30) {
                    filteredMatchingPost.addAll(
                            matchingPostRepository.findFilteredMatchingPostByMap(
                                    null,
                                    requestMap.getRecruitmentType(),
                                    requestMap.getAbility(),
                                    requestMap.getGender(),
                                    requestMap.getRecruiterType(),
                                    requestMap.getExcludeExpired() == null ? null : LocalDate.now(),
                                    requestMap.getDateFilter(),
                                    requestMap.getMonthFilter(),
                                    requestMap.getSortOption(),
                                    requestMap.getLastExpiredDate(),
                                    requestMap.getSportsType(),
                                    requestMap.getTopLat(),
                                    requestMap.getBottomLat(),
                                    requestMap.getLeftLon(),
                                    requestMap.getRightLon(),
                                    requestMap.isExcludeRegionUnit(),
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

    private List<MatchingPost> getMatchingPostByMapDetail(MatchingPostMapDto.RequestMapDetail requestMapDetail) {
        return matchingPostRepository.findFilteredMatchingPostByMapDetail(
                requestMapDetail.getRecruitmentType(),
                requestMapDetail.getAbility(),
                requestMapDetail.getGender(),
                requestMapDetail.getRecruiterType(),
                requestMapDetail.getExcludeExpired() == null ? null : LocalDate.now(),
                requestMapDetail.getDateFilter(),
                requestMapDetail.getMonthFilter(),
                requestMapDetail.getLastExpiredDate(),
                requestMapDetail.getSportsType(),
                requestMapDetail.getTopLat(),
                requestMapDetail.getBottomLat(),
                requestMapDetail.getLeftLon(),
                requestMapDetail.getRightLon(),
                requestMapDetail.isExcludeRegionUnit()
        );
    }

    public LinkedHashMap<String, Object> getFilteredMatchingPostByMapDetail(String memberId, MatchingPostMapDto.RequestMapDetail requestMapDetail) {
        List<MatchingPost> filteredMatchingPost;
        filteredMatchingPost = getMatchingPostByMapDetail(requestMapDetail);

        LinkedHashMap<String, Object> responseObj = createResponseObj(filteredMatchingPost, null);

        List<BookmarkedMatchingPost> bookmarkedByMatchingPosts = bookmarkMatchingPostRepository.findBookmarkedByMatchingPosts(filteredMatchingPost, memberId);
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
            responseObj.put("next url", serverIp + "/post/match/calendar?"+ nextRetrieveUrlParams);
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
                            .writer(
                                    post.getWriter().getTeam().getTeamType().equals(TeamType.INDIVIDUAL) ?
                                    post.getWriter().getMember().getMemberId() : post.getWriter().getTeam().getTeamName()
                            )
                            .matchingDate(post.getMatchingDate())
                            .areaList(
                                    post.getLocationName().isEmpty() ?
                                    post.getAreaList().stream().map(matchingPostArea -> matchingPostArea.getAreaName().toString()).toList() : List.of(post.getLocationName())
                            )
                            .ability(post.getAbility())
                            .isLocationConsensusPossible(post.isLocationConsensusPossible())
                            .contents(post.getContent())
                            .matchingStatus(post.getMatchingStatus())
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

    private HashMap<Long, Object> createMatchingPostDtoMapDetail(List<MatchingPost> filteredMatchingPost, List<BookmarkedMatchingPost> bookmarkedByMatchingPosts, List<Matching> matchingResultByMemberId) {
        HashMap<Long, Object> postsMap = new LinkedHashMap<>();

        filteredMatchingPost.forEach(post -> {
            boolean isBookmarked = bookmarkedByMatchingPosts.stream().anyMatch(bookmark -> bookmark.getMatchingPost().equals(post));
            boolean isMatchedBefore = matchingResultByMemberId.stream().anyMatch(matching -> matching.getMatchingPost().equals(post));

            postsMap.put(post.getPk(), MatchingPostDto.ResponseMatchingPostByMapDetailDto.builder()
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

    private List<MatchingPost> getMatchingPostByCalendar(MatchingPostByCalendarVo matchingPostByCalendarVo) {
        List<MatchingPost> filteredMatchingPost;
        switch (Objects.requireNonNull(matchingPostByCalendarVo.getSortOption())) {

            case NEW -> filteredMatchingPost = matchingPostRepository.findFilteredMatchingPostByCalendar(
                    matchingPostByCalendarVo.getLastIdx(),
                    matchingPostByCalendarVo.getRecruitmentType(),
                    matchingPostByCalendarVo.getAbility(),
                    matchingPostByCalendarVo.getGender(),
                    matchingPostByCalendarVo.getRecruiterType(),
                    matchingPostByCalendarVo.getAreaList(),
                    matchingPostByCalendarVo.getExcludeExpired() == null ? null : LocalDate.now(),
                    matchingPostByCalendarVo.getDateFilter(),
                    matchingPostByCalendarVo.getMonthFilter(),
                    matchingPostByCalendarVo.getSortOption(),
                    null,
                    matchingPostByCalendarVo.getSportsType(),
                    PageRequest.of(0, 30)
            );
            case DEADLINE -> {
                int pageNumber = Optional.ofNullable(matchingPostByCalendarVo.getCallCnt()).orElse(0);
                filteredMatchingPost = matchingPostRepository.findFilteredMatchingPostByCalendar(
                        null,
                        matchingPostByCalendarVo.getRecruitmentType(),
                        matchingPostByCalendarVo.getAbility(),
                        matchingPostByCalendarVo.getGender(),
                        matchingPostByCalendarVo.getRecruiterType(),
                        matchingPostByCalendarVo.getAreaList(),
                        matchingPostByCalendarVo.getExcludeExpired() == null ? null : LocalDate.now(),
                        matchingPostByCalendarVo.getDateFilter(),
                        matchingPostByCalendarVo.getMonthFilter(),
                        matchingPostByCalendarVo.getSortOption(),
                        matchingPostByCalendarVo.getLastExpiredDate(),
                        matchingPostByCalendarVo.getSportsType(),
                        PageRequest.of(pageNumber, 30)
                );
                removeDuplicatePosts(matchingPostByCalendarVo.getLastIdx(), filteredMatchingPost);

                if (filteredMatchingPost.size() < 30) {
                    filteredMatchingPost.addAll(
                            matchingPostRepository.findFilteredMatchingPostByCalendar(
                                    null,
                                    matchingPostByCalendarVo.getRecruitmentType(),
                                    matchingPostByCalendarVo.getAbility(),
                                    matchingPostByCalendarVo.getGender(),
                                    matchingPostByCalendarVo.getRecruiterType(),
                                    matchingPostByCalendarVo.getAreaList(),
                                    matchingPostByCalendarVo.getExcludeExpired() == null ? null : LocalDate.now(),
                                    matchingPostByCalendarVo.getDateFilter(),
                                    matchingPostByCalendarVo.getMonthFilter(),
                                    matchingPostByCalendarVo.getSortOption(),
                                    matchingPostByCalendarVo.getLastExpiredDate(),
                                    matchingPostByCalendarVo.getSportsType(),
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
        while (iterator.hasNext()&&!Objects.isNull(lastIdx)&& filteredMatchingPost.stream().anyMatch(matchingPost -> matchingPost.getPk().equals(lastIdx))) {
            MatchingPost next = iterator.next();
            iterator.remove();
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
        Member member = memberService.findByMemberId(memberId);
        List<MatchingPost> matchingPostWrittenReview = teamReviewRepository.findMatchingPostWithMemberId(member);
        List<MatchingPost> matchingPosts = matchingPostRepository.findCompletedMatchingPosts(nextIdx, recruiterType, excludeCompleteMatchesFilter, member, matchingPostWrittenReview);
        List<TeamMember> managerOrHigherTeamMembers = teamMemberRepository.findWithManagerOrHigher(getTeamsWithMatchingPosts(matchingPosts));
        LinkedHashMap<Long, MatchingPostDto.ResponseCompletedMatchingPost> responseHashMap = new LinkedHashMap<>();

        matchingPosts.forEach(matchingPost -> responseHashMap.put(matchingPost.getPk(), MatchingPostDto.ResponseCompletedMatchingPost.builder()
                .matchingDate(matchingPost.getMatchingDate())
                .recruiterType(matchingPost.getRecruiterType())
                .nickname(getNickname(matchingPost))
                .completedMatchingCnt(matchingPost.getTeam().getCompletedMatchingCnt())
                .content(matchingPost.getContent())
                .areaNames(getAreas(matchingPost))
                .isLocationConsensusPossible(matchingPost.isLocationConsensusPossible())
                .ability(matchingPost.getAbility())
                .profileImgUrl(getProfileImgUrl(matchingPost))
                .matchingStatus(matchingStatusFactory(matchingPost.getMatchingStatus(), matchingPost.getMatchingDate()))
                .scheduledRequestDescription(getScheduledRequest(matchingPost, managerOrHigherTeamMembers, member, matchingPostWrittenReview))
                .build()));

        return responseHashMap;
    }

    private List<Team> getTeamsWithMatchingPosts(List<MatchingPost> matchingPosts) {
        return matchingPosts.stream().map(MatchingPost::getTeam).toList();
    }

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
                ? createScheduledRequestWithOwner(matchingPost.getMatchingStatus(), matchingPost.getMatchingDate(), matchingPost, matchingPostWrittenReview)
                : createScheduledRequestWithMember(matchingPost.getMatchingStatus(), matchingPost, matchingPostWrittenReview);
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
    public void saveMatchingPost(MatchingPostDto.CreateMatchingPostDto createDto, String writerId){
        List<Long> participantsTeamMemberId = createDto.getParticipantsId();

        Team team = teamRepository.findById(createDto.getTeamPk())
                .orElseThrow(() -> new EntityNotFoundException(TEAM_NOT_FOUND.getExceptionMessage()));
        TeamMember writerInTeam = teamMemberService.findByTeamAndMember_MemberId(team, writerId);

        MatchingPost matchingPost = createDto.of(team, writerInTeam, createDto.getMatchingDate());
        matchingPostRepository.save(matchingPost);

        Member teamOwner = memberService.findByMemberId(writerId);
        saveMatchingOwner(team, teamOwner, matchingPost);

        if (isExistTeamParticipant(createDto.getRecruiterType(), participantsTeamMemberId)){
            List<Member> memberList = teamMemberRepository.findMemberByTeamMemberIdIn(participantsTeamMemberId);
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

    private boolean isExistTeamParticipant(RecruiterType recruiterType, List<Long> participantsPk) {
        return recruiterType.equals(RecruiterType.TEAM) && !participantsPk.isEmpty();
    }

    public List<MatchingPostMapDto.ResponseClusterMapDetail> getMatchingPostByMap(double topLat,
                                     double bottomLat,
                                     double leftLon,
                                     double rightLon,
                                     int heightTileCnt,
                                     int widthTileCnt) {
        List<MatchingPost> matchingPostInMap = matchingPostRepository.findMatchingPostInMap(topLat, bottomLat, leftLon, rightLon);

        double latTileRange = (topLat - bottomLat) / heightTileCnt;
        double lonTileRange = (rightLon - leftLon) / widthTileCnt;

        List<MatchingPostMapDto.ResponseClusterMapDetail> clusterData = new LinkedList<>();

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
                                            clusterData.add(MatchingPostMapDto.ResponseClusterMapDetail.builder()
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
    public void rePostMatchingPost(Long postId, String memberId){
        Member requester = memberService.findByMemberId(memberId);
        MatchingPost matchingPost = getMatchingPostByPostId(postId);

        if (!matchingPost.isWriter(requester)) {
            throw new NotWriterException();
        }

        List<BookmarkedMatchingPost> bookmarkedMatchingPosts = bookmarkMatchingPostRepository.findByMatchingPost(matchingPost);
        List<Matching> matchings = matchingRepository.findByMatchingPost(matchingPost);
        matchingRepository.deleteAllInBatch(matchings);
        List<MatchingRequest> matchingRequests = matchingRequestRepository.findByMatchingPost(matchingPost);
        matchingRequestRepository.deleteAllInBatch(matchingRequests);
        //todo 신고 db 완성되면 신고 기록도 삭제

        MatchingPost reCreateMatchingPost = matchingPost.reCreateMatchingPost();
        matchingPostRepository.save(reCreateMatchingPost);

        updateBookmarkedWithNewPost(bookmarkedMatchingPosts, reCreateMatchingPost);
        reSaveAreas(matchingPost, reCreateMatchingPost);

        matchingPostRepository.delete(matchingPost);
    }

    private void updateBookmarkedWithNewPost(List<BookmarkedMatchingPost> bookmarkedMatchingPosts, MatchingPost reCreateMatchingPost) {
        bookmarkedMatchingPosts.forEach(bookmarkedMatchingPost -> bookmarkedMatchingPost.updateMatchingPost(reCreateMatchingPost));
    }

    private void reSaveAreas(MatchingPost matchingPost, MatchingPost reCreateMatchingPost) {
        List<MatchingPostArea> reCreateMatchingAreas = matchingPost.getAreaList().stream()
                .map(area -> MatchingPostArea.builder()
                        .matchingPost(reCreateMatchingPost)
                        .areaName(area.getAreaName())
                        .build())
                .toList();

        matchingPostAreaRepository.saveAll(reCreateMatchingAreas);
    }

    @Transactional
    public void completeMatchingPost(Long matchingPostPk){
        MatchingPost matchingPost = findById(matchingPostPk);

        matchingPost.complete();
    }

    public HashMap<Long, MatchingPostDto.ResponseTop15PopularPost> getTop15PopularPost(SportsType sportsType){
        List<MatchingPost> top15PopularPost = matchingPostRepository.findTop15PopularPost(sportsType);
        LinkedHashMap<Long, MatchingPostDto.ResponseTop15PopularPost> responseData = new LinkedHashMap<>();

        top15PopularPost.forEach(matchingPost -> responseData.put(matchingPost.getPk(), MatchingPostDto.ResponseTop15PopularPost.builder()
                .imgUrl(getProfileImgUrl(matchingPost))
                .nickname(getNickname(matchingPost))
                .areas(getAreas(matchingPost))
                .matchingCnt(matchingPost.getTeam().getCompletedMatchingCnt())
                .matchingDate(matchingPost.getMatchingDate())
                .expiryDate(matchingPost.getExpiryDate())
                .recruiterType(matchingPost.getRecruiterType())
                .ability(matchingPost.getAbility())
                .isLocationConsensusPossible(matchingPost.isLocationConsensusPossible())
                .build()));

        return responseData;
    }

    private String getNickname(MatchingPost matchingPost){
        return isTeam(matchingPost)
                ? matchingPost.getTeam().getTeamName()
                : matchingPost.getWriter().getNickname();
    }

    private List<String> getAreas(MatchingPost matchingPost){
        return matchingPost.getLocationSelectionType().equals(LocationSelectionType.SEARCH_BASED)
                ? List.of(matchingPost.getLocationName())
                : matchingPost.getAreaList().stream().map(matchingPostArea -> matchingPostArea.getAreaName().toString()).toList();
    }

    public HashMap<Long, MatchingPostDto.ResponseTop200PopularPost> getTop200PopularPost(SportsType sportsType, String memberId){
        List<MatchingPost> top200PopularPost = matchingPostRepository.findTop200PopularPost(sportsType);
        LinkedHashMap<Long, MatchingPostDto.ResponseTop200PopularPost> responseData = new LinkedHashMap<>();
        List<BookmarkedMatchingPost> bookmarkedMatchingPosts = bookmarkMatchingPostRepository.findBookmarkedByMatchingPosts(top200PopularPost, memberId);

        top200PopularPost.forEach(matchingPost ->
            responseData.put(matchingPost.getPk(), MatchingPostDto.ResponseTop200PopularPost.builder()
                .imgUrl(getProfileImgUrl(matchingPost))
                .nickname(getNickname(matchingPost))
                .content(matchingPost.getContent())
                .areas(getAreas(matchingPost))
                .matchingCnt(matchingPost.getTeam().getCompletedMatchingCnt())
                .matchingDate(matchingPost.getMatchingDate())
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
        List<BookmarkedMatchingPost> bookmarkedMatchingPosts = bookmarkMatchingPostRepository.findBookmarkedByMatchingPosts(recentPost, memberId);

        recentPost.forEach(matchingPost ->
                responseData.put(matchingPost.getPk(), MatchingPostDto.ResponseRecentPost.builder()
                        .imgUrl(getProfileImgUrl(matchingPost))
                        .nickname(getNickname(matchingPost))
                        .content(matchingPost.getContent())
                        .areas(getAreas(matchingPost))
                        .matchingCnt(matchingPost.getTeam().getCompletedMatchingCnt())
                        .matchingDate(matchingPost.getMatchingDate())
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

    public HashMap<String, Object> getSearchPost(MatchingPostDto.RequestSearchPost searchDto, String memberId){
        List<MatchingPost> searchMatchingPosts = getSearchMatchingPosts(searchDto.getQuery(), searchDto.getLastIdx(), searchDto.getLastExpiredDate(), searchDto.getCallCnt(), searchDto.getSortOption());
        List<BookmarkedMatchingPost> bookmarked = bookmarkMatchingPostRepository.findBookmarkedByMatchingPosts(searchMatchingPosts, memberId);

        Integer nextUrlCallCnt = createNextUrlCallCnt(searchDto.getCallCnt(), searchMatchingPosts);
        String nextUrl = createNextRetrieveUrlParamsBySearch(getLastIdxInMatchingPostList(searchMatchingPosts), searchDto.getSortOption(), getLastExpiredDateInMatchingPostList(searchDto.getSortOption(), searchMatchingPosts), searchDto.getQuery(), nextUrlCallCnt);
        HashMap<Long, MatchingPostDto.ResponseSearchPost> responsePostList = createResponsePostList(searchMatchingPosts, bookmarked);

        return createResponseData(responsePostList, nextUrl);
    }

    private HashMap<String, Object> createResponseData(HashMap<Long, MatchingPostDto.ResponseSearchPost> responsePostList, String nextUrl) {
        nextUrl = responsePostList.isEmpty() ? null : serverIp + SEARCH_MATCHING_POST_PATH + "?" + nextUrl;
        HashMap<String, Object> responseData = new HashMap<>();
        responseData.put("nextUrl", nextUrl);
        responseData.put("postList", responsePostList);

        return responseData;
    }

    private HashMap<Long, MatchingPostDto.ResponseSearchPost> createResponsePostList(List<MatchingPost> searchMatchingPosts, List<BookmarkedMatchingPost> bookmarked) {
        LinkedHashMap<Long, MatchingPostDto.ResponseSearchPost> postList = new LinkedHashMap<>();
        searchMatchingPosts.forEach(post -> postList.put(post.getPk(), MatchingPostDto.ResponseSearchPost.builder()
                .imgUrl(getProfileImgUrl(post))
                .nickname(getNickname(post))
                .content(post.getContent())
                .areas(getAreas(post))
                .matchingCnt(post.getTeam().getCompletedMatchingCnt())
                .matchingDate(post.getMatchingDate())
                .recruiterType(post.getRecruiterType())
                .ability(post.getAbility())
                .isLocationConsensusPossible(post.isLocationConsensusPossible())
                .isBookmarked(isBookmarked(post, bookmarked))
                .isExpired(isExpired(post))
                .build()));

        return postList;
    }

    private List<MatchingPost> getSearchMatchingPosts(String query, Long lastIdx, LocalDate lastExpiredDate, Integer callCnt, SortOption sortOption) {
        List<MatchingPost> matchingPosts;
        switch (sortOption){
            case NEW -> {
                matchingPosts = matchingPostRepository.findSearchMatchingPost(
                        query,
                        lastIdx,
                        lastExpiredDate,
                        sortOption,
                        PageRequest.of(0, PAGE_SIZE));
            }
            case DEADLINE -> {
                int pageNumber = callCnt == null ? 0 : callCnt;
                matchingPosts = matchingPostRepository.findSearchMatchingPost(
                        query,
                        null,
                        lastExpiredDate,
                        sortOption,
                        PageRequest.of(pageNumber, PAGE_SIZE));
                removeDuplicatePosts(lastIdx, matchingPosts);

                if (matchingPosts.size() < 30){
                    matchingPosts.addAll(
                            matchingPostRepository.findSearchMatchingPost(
                                    query,
                                    null,
                                    lastExpiredDate,
                                    sortOption,
                                    PageRequest.of(pageNumber + 1, PAGE_SIZE))
                    );
                }
                matchingPosts = matchingPosts.stream().limit(PAGE_SIZE).toList();
            }
            default -> throw new RuntimeException("unknown enum value");
        }
        return matchingPosts;
    }

    private String createNextRetrieveUrlParamsBySearch(Long lastIdx, SortOption sortOption, LocalDate lastExpiredDate, String query, Integer callCnt) {
        Map<String, Object> parameters = new LinkedHashMap<>();
        parameters.put("lastIdx", lastIdx);
        parameters.put("sortOption", sortOption);
        parameters.put("lastExpiredDate", lastExpiredDate);
        parameters.put("query", query);
        if (sortOption.equals(SortOption.DEADLINE)) {
            parameters.put("callCnt", callCnt);
        }

        return parameters.entrySet().stream()
                .filter(entry -> entry.getValue() != null)
                .map(entry -> entry.getKey() + "=" + entry.getValue())
                .collect(Collectors.joining("&"));
    }

    public boolean isWriter(MatchingPost matchingPost, Member member){
        return matchingPost.getWriter().getMember().equals(member);
    }

    public boolean isDeletable(Team writerTeam, List<Matching> matchings){
        return matchings.stream()
                .allMatch(matching -> matching.getTeam().equals(writerTeam));
    }

    @Transactional
    public void deleteMatchingPost(MatchingPost matchingPost, List<Matching> matchings){
        List<BookmarkedMatchingPost> bookmarkedMatchingPosts = bookmarkMatchingPostRepository.findByMatchingPost(matchingPost);

        //todo 신고 글 서비스 구현되면 신고 된 내역도 삭제
        bookmarkMatchingPostRepository.deleteAllInBatch(bookmarkedMatchingPosts);
        matchingRequestRepository.deleteAllInBatch(matchingRequestRepository.findByMatchingPost(matchingPost));
        matchingRepository.deleteAllInBatch(matchings);
        matchingPostRepository.delete(matchingPost);
    }

    @Transactional
    public void updateMatchingPostContent(Long matchingPostPk, String content){
        MatchingPost matchingPost = findById(matchingPostPk);

        matchingPost.updateContent(content);
    }


    public MatchingPostDto.ResponseMatchingPostDetail getMatchingPostDetail(Long matchingPostPk, String memberId){
        TeamRatingUtil teamRatingUtil = new TeamRatingUtil();
        Member requester = memberService.findByMemberId(memberId);
        MatchingPost matchingPost = findById(matchingPostPk);
        Team writerTeam = matchingPost.getTeam();
        Optional<TeamMember> requesterTeamMember = teamMemberRepository.findByTeamAndMember(writerTeam, requester);
        Integer reviewCnt = teamReviewRepository.findTeamReviewCntWithReviewee(writerTeam);
        Double totalRating = teamRatingRepository.findTotalRatingWithTeam(writerTeam);

        return MatchingPostDto.ResponseMatchingPostDetail.builder()
                .teamCreateDate(writerTeam.getCreatedTime().toLocalDate())
                .teamMemberCnt(writerTeam.getTeamMembers().size())
                .teamImgUrl(s3ImgService.getGroupProfilePicUrl(writerTeam.getProfileImgId()))
                .teamName(writerTeam.getTeamName())
                .teamRating(teamRatingUtil.adjustTeamRating(getNonNullTotalRating(totalRating)))
                .reviewCnt(getNonNullReviewCnt(reviewCnt))
                .matchingDate(matchingPost.getMatchingDate())
                .areas(getAreas(matchingPost))
                .ability(matchingPost.getAbility())
                .participantsCnt(matchingPost.getMyCapacityCount())
                .participantsImgUrls(getParticipantsImgUrls(matchingPost))
                .recruiterType(matchingPost.getRecruiterType())
                .expiryDate(matchingPost.getExpiryDate())
                .recruitmentType(matchingPost.getRecruitmentType())
                .isBookmarked(bookmarkMatchingPostRepository.existsByMatchingPostAndMember(matchingPost, requester))
                .isCompleted(matchingPost.isComplete())
                .isWriter(isWriter(requesterTeamMember, matchingPost.getWriter()))
                .build();
    }

    private double getNonNullTotalRating(Double totalRating){
        return totalRating == null ? 0 : totalRating;
    }

    private int getNonNullReviewCnt(Integer reviewCnt) {
        return reviewCnt == null ? 0 : reviewCnt;
    }

    private List<String> getParticipantsImgUrls(MatchingPost matchingPost){
        List<Member> participants = matchingRepository.findMatchingPostTeamParticipants(matchingPost.getTeam(), matchingPost);
        List<TeamMember> teamMembers = teamMemberRepository.findByTeamAndMemberIn(matchingPost.getTeam(), participants);

        return teamMembers.stream()
                .map(teamMember -> s3ImgService.getTeamMemberPreSignedUrl(teamMember.getProfileImg()))
                .toList();
    }

    private boolean isWriter(Optional<TeamMember> requesterTeamMember, TeamMember writer) {
        return requesterTeamMember.isPresent() ? writer.equals(requesterTeamMember.get()) : false;
    }

    //todo 근처의 글 조회 서비스 로직 구현
//    public HashMap<String, List<MatchingPostDto.ResponsePostByArea>> getMatchingPostByArea(List<String> dou, SportsType sportsType, String memberId){
//        List<MatchingPost> top200PopularPost = matchingPostRepository.findTop200PopularPost(sportsType);
//        LinkedHashMap<Long, List<MatchingPostDto.ResponsePostByArea>> responseData = new LinkedHashMap<>();
//        List<BookmarkedMatchingPost> bookmarkedMatchingPosts = bookmarkRepository.findBookmarkedByMatchingPosts(top200PopularPost, memberId);
//
//        top200PopularPost.forEach(matchingPost ->
//                responseData.put(matchingPost.getPk(), MatchingPostDto.ResponseTop200PopularPost.builder()
//                        .imgUrl(getProfileImgUrl(matchingPost))
//                        .nickname(getNickname(matchingPost))
//                        .content(matchingPost.getContent())
//                        .areas(getAreas(matchingPost))
//                        .matchingCnt(matchingPost.getTeam().getCompletedMatchingCnt())
//                        .matchingDate(getMatchingDates(matchingPost))
//                        .recruiterType(matchingPost.getRecruiterType())
//                        .ability(matchingPost.getAbility())
//                        .isLocationConsensusPossible(matchingPost.isLocationConsensusPossible())
//                        .isBookmarked(isBookmarked(matchingPost, bookmarkedMatchingPosts))
//                        .isExpired(isExpired(matchingPost))
//                        .build()));
//
//        return responseData;
//    }

    public MatchingPost findById(Long postId) {
        return matchingPostRepository.findById(postId)
                .orElseThrow(() -> new EntityNotFoundException(ExceptionMessage.POST_NOT_FOUND.getExceptionMessage()));
    }
}
