package com.wemingle.core.domain.post.controller;

import com.wemingle.core.domain.category.sports.entity.sportstype.SportsType;
import com.wemingle.core.domain.member.entity.Member;
import com.wemingle.core.domain.member.service.MemberService;
import com.wemingle.core.domain.post.dto.MatchingPostDto;
import com.wemingle.core.domain.post.dto.MatchingPostMapDto;
import com.wemingle.core.domain.post.dto.sortoption.SortOption;
import com.wemingle.core.domain.post.entity.MatchingPost;
import com.wemingle.core.domain.post.entity.abillity.Ability;
import com.wemingle.core.domain.post.entity.area.AreaName;
import com.wemingle.core.domain.post.entity.gender.Gender;
import com.wemingle.core.domain.post.entity.recruitertype.RecruiterType;
import com.wemingle.core.domain.post.service.MatchingPostService;
import com.wemingle.core.domain.post.service.TeamPostService;
import com.wemingle.core.domain.team.entity.recruitmenttype.RecruitmentType;
import com.wemingle.core.global.responseform.ResponseHandler;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.time.YearMonth;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/post/match")
public class MatchingPostController {
    private final MatchingPostService matchingPostService;
    private final TeamPostService teamPostService;
    private final MemberService memberService;
    @PostMapping
    ResponseEntity<ResponseHandler<Object>> createMatchingPost(@RequestBody @Valid MatchingPostDto.CreateMatchingPostDto matchingPostDto,
                                                               @AuthenticationPrincipal UserDetails userDetails) {
        matchingPostService.saveMatchingPost(matchingPostDto, userDetails.getUsername());
        return ResponseEntity.ok() //todo 글 상세페이지 조회 api 구현 후 201로 변경하고 url 함께 반환
                .body(
                        ResponseHandler.builder()
                                .responseMessage("matching post successfully created")
                                .build()
                );
    }

    @GetMapping("/my")
    ResponseEntity<ResponseHandler<Object>> getAllMyPosts(@RequestParam(value = "nextIdx",required = false) Long nextIdx,
                                                          @RequestParam("recruiterType") RecruiterType recruiterType,
                                                          @AuthenticationPrincipal UserDetails userDetails) {
        HashMap<Long, Object> allMyPosts = matchingPostService.getAllMyPosts(nextIdx, recruiterType, userDetails.getUsername());
        return ResponseEntity.ok(
                ResponseHandler.builder()
                        .responseMessage("My posts retrieval successfully")
                        .responseData(allMyPosts)
                        .build()
        );
    }

    @GetMapping("/group")
    ResponseEntity<ResponseHandler<Object>> getAllMyPosts(@RequestParam("nextIdx") Long nextIdx,
                                                          @RequestParam("teamId") Long teamId,
                                                          @AuthenticationPrincipal UserDetails userDetails) {
        HashMap<Long, Object> myTeamPosts = teamPostService.getMyTeamPosts(nextIdx, teamId, userDetails.getUsername());
        return ResponseEntity.ok(
                ResponseHandler.builder()
                        .responseMessage("My team posts retrieval successfully")
                        .responseData(myTeamPosts)
                        .build()
        );
    }

    @GetMapping("/calendar")
    public ResponseEntity<ResponseHandler<Object>> getMatchingPostByCalender(@RequestParam SortOption sortOption,
                                                                             @RequestParam(required = false) Long lastIdx,
                                                                             @RequestParam(required = false) RecruitmentType recruitmentType,
                                                                             @RequestParam(required = false) Ability ability,
                                                                             @RequestParam(required = false) Gender gender,
                                                                             @RequestParam(required = false) RecruiterType recruiterType,
                                                                             @RequestParam(required = false) List<AreaName> areaList,
                                                                             @RequestParam(required = false) @DateTimeFormat(pattern = "yyyy-MM-dd") LocalDate dateFilter,
                                                                             @RequestParam(required = false) @DateTimeFormat(pattern = "yyyy-MM") YearMonth monthFilter,
                                                                             @RequestParam(required = false) @DateTimeFormat(pattern = "yyyy-MM-dd") LocalDate lastExpiredDate,
                                                                             @RequestParam(required = false) Boolean excludeExpired,
                                                                             @RequestParam(required = false) Integer callCnt,
                                                                             @RequestParam SportsType sportsType,
                                                                             @AuthenticationPrincipal UserDetails userDetails){
        HashMap<String, Object> getFilteredMatchingPost = matchingPostService.getFilteredMatchingPostByCalendar(userDetails.getUsername(),
                lastIdx,
                recruitmentType,
                ability,
                gender,
                recruiterType,
                areaList,
                dateFilter,
                monthFilter,
                excludeExpired,
                sortOption,
                lastExpiredDate,
                callCnt,
                sportsType);

        return ResponseEntity.ok(
                ResponseHandler.builder()
                        .responseMessage("matching posts retrieval successfully")
                        .responseData(getFilteredMatchingPost).build()
        );
    }

    @GetMapping("/calendar/count")
    public ResponseEntity<ResponseHandler<Object>> getMatchingPostByCalenderCnt(@RequestParam(required = false) RecruitmentType recruitmentType,
                                                                                @RequestParam(required = false) Ability ability,
                                                                                @RequestParam(required = false) Gender gender,
                                                                                @RequestParam(required = false) RecruiterType recruiterType,
                                                                                @RequestParam(required = false) List<AreaName> areaList,
                                                                                @RequestParam(required = false) @DateTimeFormat(pattern = "yyyy-MM-dd") LocalDate dateFilter,
                                                                                @RequestParam(required = false) @DateTimeFormat(pattern = "yyyy-MM") YearMonth monthFilter,
                                                                                @RequestParam(required = false) Boolean excludeExpired,
                                                                                @RequestParam SportsType sportsType){
        Integer filteredMatchingPostCnt = matchingPostService.getFilteredMatchingPostCnt(recruitmentType,
                ability,
                gender,
                recruiterType,
                areaList,
                dateFilter,
                monthFilter,
                excludeExpired,
                sportsType);

        return ResponseEntity.ok(
                ResponseHandler.builder()
                        .responseMessage("total matching post count retrieval successfully")
                        .responseData(filteredMatchingPostCnt).build()
        );
    }

    @GetMapping("/map/count")
    public ResponseEntity<ResponseHandler<Object>> getMatchingPostByMapCnt(@RequestParam(required = false) RecruitmentType recruitmentType,
                                                                           @RequestParam(required = false) Ability ability,
                                                                           @RequestParam(required = false) Gender gender,
                                                                           @RequestParam(required = false) RecruiterType recruiterType,
                                                                           @RequestParam(required = false) List<LocalDate> dateFilter,
                                                                           @RequestParam(required = false) @DateTimeFormat(pattern = "yyyy-MM") YearMonth monthFilter,
                                                                           @RequestParam(required = false) @DateTimeFormat(pattern = "yyyy-MM-dd") LocalDate lastExpiredDate,
                                                                           @RequestParam(required = false) Boolean excludeExpired,
                                                                           @RequestParam("topLat") double topLat,
                                                                           @RequestParam("bottomLat") double bottomLat,
                                                                           @RequestParam("leftLon") double leftLon,
                                                                           @RequestParam("rightLon") double rightLon,
                                                                           @RequestParam("excludeRegionUnit") boolean excludeRegionUnit,
                                                                           @RequestParam SportsType sportsType){
        Integer filteredMatchingPostCnt = matchingPostService.getFilteredMatchingPostByMapCnt(
                recruitmentType,
                ability,
                gender,
                recruiterType,
                dateFilter,
                monthFilter,
                excludeExpired,
                lastExpiredDate,
                sportsType,
                topLat,
                bottomLat,
                leftLon,
                rightLon,
                excludeRegionUnit);

        return ResponseEntity.ok(
                ResponseHandler.builder()
                        .responseMessage("total matching post count retrieval successfully")
                        .responseData(filteredMatchingPostCnt).build()
        );
    }

    @GetMapping("/map/detail")
    public ResponseEntity<ResponseHandler<Object>> getMatchingPostByMapDetail(@RequestParam(required = false) RecruitmentType recruitmentType,
                                                                              @RequestParam(required = false) Ability ability,
                                                                              @RequestParam(required = false) Gender gender,
                                                                              @RequestParam(required = false) RecruiterType recruiterType,
                                                                              @RequestParam(required = false) List<LocalDate> dateFilter,
                                                                              @RequestParam(required = false) @DateTimeFormat(pattern = "yyyy-MM") YearMonth monthFilter,
                                                                              @RequestParam(required = false) @DateTimeFormat(pattern = "yyyy-MM-dd") LocalDate lastExpiredDate,
                                                                              @RequestParam(required = false) Boolean excludeExpired,
                                                                              @RequestParam("topLat") double topLat,
                                                                              @RequestParam("bottomLat") double bottomLat,
                                                                              @RequestParam("leftLon") double leftLon,
                                                                              @RequestParam("rightLon") double rightLon,
                                                                              @RequestParam("excludeRegionUnit") boolean excludeRegionUnit,
                                                                              @RequestParam SportsType sportsType,
                                                                              @AuthenticationPrincipal UserDetails userDetails) {
        HashMap<String, Object> getFilteredMatchingPost = matchingPostService.getFilteredMatchingPostByMapDetail(userDetails.getUsername(),
                recruitmentType,
                ability,
                gender,
                recruiterType,
                dateFilter,
                monthFilter,
                excludeExpired,
                lastExpiredDate,
                sportsType,
                topLat,
                bottomLat,
                leftLon,
                rightLon,
                excludeRegionUnit);

        return ResponseEntity.ok(
                ResponseHandler.builder()
                        .responseMessage("matching posts retrieval successfully")
                        .responseData(getFilteredMatchingPost).build()
        );
    }

    @GetMapping("/map")
    public ResponseEntity<ResponseHandler<Object>> getMatchingPostByMap(@RequestParam SortOption sortOption,
                                                                        @RequestParam(required = false) Long lastIdx,
                                                                        @RequestParam(required = false) RecruitmentType recruitmentType,
                                                                        @RequestParam(required = false) Ability ability,
                                                                        @RequestParam(required = false) Gender gender,
                                                                        @RequestParam(required = false) RecruiterType recruiterType,
                                                                        @RequestParam(required = false) @DateTimeFormat(pattern = "yyyy-MM-dd") List<LocalDate> dateFilter,
                                                                        @RequestParam(required = false) @DateTimeFormat(pattern = "yyyy-MM") YearMonth monthFilter,
                                                                        @RequestParam(required = false) @DateTimeFormat(pattern = "yyyy-MM-dd") LocalDate lastExpiredDate,
                                                                        @RequestParam(required = false) Boolean excludeExpired,
                                                                        @RequestParam(required = false) Integer callCnt,
                                                                        @RequestParam("topLat") double topLat,
                                                                        @RequestParam("bottomLat") double bottomLat,
                                                                        @RequestParam("leftLon") double leftLon,
                                                                        @RequestParam("rightLon") double rightLon,
                                                                        @RequestParam("excludeRegionUnit") boolean excludeRegionUnit,
                                                                        @RequestParam SportsType sportsType,
                                                                        @AuthenticationPrincipal UserDetails userDetails){
        HashMap<String, Object> getFilteredMatchingPost = matchingPostService.getFilteredMatchingPostByMap(userDetails.getUsername(),
                lastIdx,
                recruitmentType,
                ability,
                gender,
                recruiterType,
                dateFilter,
                monthFilter,
                excludeExpired,
                sortOption,
                lastExpiredDate,
                callCnt,
                sportsType,
                topLat,
                bottomLat,
                leftLon,
                rightLon,
                excludeRegionUnit);

        return ResponseEntity.ok(
                ResponseHandler.builder()
                        .responseMessage("matching posts retrieval successfully")
                        .responseData(getFilteredMatchingPost).build()
        );
    }

    @GetMapping("/completion")
    public ResponseEntity<ResponseHandler<LinkedHashMap<Long, MatchingPostDto.ResponseCompletedMatchingPost>>> getCompletedMatchingPosts(@RequestParam(required = false) Long nextIdx,
                                                                                                                                         @RequestParam(required = false) RecruiterType recruiterType,
                                                                                                                                         @RequestParam boolean excludeCompleteMatchesFilter,
                                                                                                                                         @AuthenticationPrincipal UserDetails userDetails) {
        LinkedHashMap<Long, MatchingPostDto.ResponseCompletedMatchingPost> completedMatchingPosts =
                matchingPostService.getCompletedMatchingPosts(nextIdx, recruiterType, excludeCompleteMatchesFilter, userDetails.getUsername());

        return ResponseEntity.ok(ResponseHandler.<LinkedHashMap<Long, MatchingPostDto.ResponseCompletedMatchingPost>>builder()
                .responseMessage("completed matching posts retrieval successfully")
                .responseData(completedMatchingPosts)
                .build());
    }

    @GetMapping("/map/cluster")
    public ResponseEntity<ResponseHandler<Object>> getMatchingPostByMap(@RequestParam("topLat") double topLat,
                                                                        @RequestParam("bottomLat") double bottomLat,
                                                                        @RequestParam("leftLon") double leftLon,
                                                                        @RequestParam("rightLon") double rightLon,
                                                                        @RequestParam("heightTileCnt") int heightTileCnt,
                                                                        @RequestParam("widthTileCnt") int widthTileCnt) {
        List<MatchingPostMapDto> matchingPostByMap = matchingPostService.getMatchingPostByMap(topLat, bottomLat, leftLon, rightLon, heightTileCnt, widthTileCnt);
        return ResponseEntity.ok(ResponseHandler.builder().responseMessage("completed location data clustering successfully").responseData(matchingPostByMap).build());
    }

    @PatchMapping("/re/{matchingPostPk}")
    public ResponseEntity<?> rePostMatchingPost(@PathVariable Long matchingPostPk,
                                                @AuthenticationPrincipal UserDetails userDetails){
        Member member = memberService.findByMemberId(userDetails.getUsername());
        MatchingPost matchingPost = matchingPostService.getMatchingPostByPostId(matchingPostPk);

        if (!matchingPost.getWriter().getMember().equals(member)) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN)
                    .body(ResponseHandler.builder()
                            .responseMessage("RePost is only available for writer")
                            .build());
        }

        matchingPostService.rePostMatchingPost(matchingPost);

        return ResponseEntity.noContent().build();
    }

    @PatchMapping("/completion")
    public ResponseEntity<Object> completeMatchingPost(@RequestBody @Valid MatchingPostDto.RequestComplete requestDto){
        matchingPostService.completeMatchingPost(requestDto.getMatchingPostPk());

        return ResponseEntity.noContent().build();
    }
}
