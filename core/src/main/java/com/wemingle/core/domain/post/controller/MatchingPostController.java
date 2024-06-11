package com.wemingle.core.domain.post.controller;

import com.wemingle.core.domain.category.sports.entity.sportstype.SportsType;
import com.wemingle.core.domain.matching.entity.Matching;
import com.wemingle.core.domain.matching.service.MatchingService;
import com.wemingle.core.domain.member.entity.Member;
import com.wemingle.core.domain.member.service.MemberService;
import com.wemingle.core.domain.post.dto.MatchingPostDto;
import com.wemingle.core.domain.post.dto.MatchingPostMapDto;
import com.wemingle.core.domain.post.entity.MatchingPost;
import com.wemingle.core.domain.post.entity.recruitertype.RecruiterType;
import com.wemingle.core.domain.post.service.MatchingPostService;
import com.wemingle.core.domain.post.service.TeamPostService;
import com.wemingle.core.global.responseform.ResponseHandler;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;

@Validated
@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/posts/match")
public class MatchingPostController {
    private final MatchingPostService matchingPostService;
    private final TeamPostService teamPostService;
    private final MemberService memberService;
    private final MatchingService matchingService;
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

    @GetMapping("/members")
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

    @GetMapping("/teams")
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
    public ResponseEntity<ResponseHandler<Object>> getMatchingPostByCalender(@ModelAttribute MatchingPostDto.RequestCalendarDto requestCalendarDto,
                                                                             @AuthenticationPrincipal UserDetails userDetails){
        HashMap<String, Object> getFilteredMatchingPost = matchingPostService.getFilteredMatchingPostByCalendar(userDetails.getUsername(),requestCalendarDto);

        return ResponseEntity.ok(
                ResponseHandler.builder()
                        .responseMessage("matching posts retrieval successfully")
                        .responseData(getFilteredMatchingPost).build()
        );
    }

    @GetMapping("/calendar/count")
    public ResponseEntity<ResponseHandler<Object>> getMatchingPostByCalenderCnt(@ModelAttribute MatchingPostDto.RequestCalenderCntDto requestCalenderCntDto){
        Integer filteredMatchingPostCnt = matchingPostService.getFilteredMatchingPostCnt(requestCalenderCntDto);

        return ResponseEntity.ok(
                ResponseHandler.builder()
                        .responseMessage("total matching post count retrieval successfully")
                        .responseData(filteredMatchingPostCnt).build()
        );
    }

    @GetMapping("/map/count")
    public ResponseEntity<ResponseHandler<Object>> getMatchingPostByMapCnt(MatchingPostMapDto.RequestMapCnt requestMapCnt){
        Integer filteredMatchingPostCnt = matchingPostService.getFilteredMatchingPostByMapCnt(requestMapCnt);

        return ResponseEntity.ok(
                ResponseHandler.builder()
                        .responseMessage("total matching post count retrieval successfully")
                        .responseData(filteredMatchingPostCnt).build()
        );
    }

    @GetMapping("/map/detail")
    public ResponseEntity<ResponseHandler<Object>> getMatchingPostByMapDetail(@ModelAttribute MatchingPostMapDto.RequestMapDetail requestMapDetail, @AuthenticationPrincipal UserDetails userDetails) {
        HashMap<String, Object> getFilteredMatchingPost = matchingPostService.getFilteredMatchingPostByMapDetail(userDetails.getUsername(), requestMapDetail);

        return ResponseEntity.ok(
                ResponseHandler.builder()
                        .responseMessage("matching posts retrieval successfully")
                        .responseData(getFilteredMatchingPost).build()
        );
    }

    @GetMapping("/map")
    public ResponseEntity<ResponseHandler<Object>> getMatchingPostByMap(@ModelAttribute MatchingPostMapDto.RequestMap requestMap,
                                                                        @AuthenticationPrincipal UserDetails userDetails){
        HashMap<String, Object> getFilteredMatchingPost = matchingPostService.getFilteredMatchingPostByMap(userDetails.getUsername(), requestMap);

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
        List<MatchingPostMapDto.ResponseClusterMapDetail> matchingPostByMap = matchingPostService.getMatchingPostByMap(topLat, bottomLat, leftLon, rightLon, heightTileCnt, widthTileCnt);
        return ResponseEntity.ok(ResponseHandler.builder().responseMessage("completed location data clustering successfully").responseData(matchingPostByMap).build());
    }

    @PatchMapping("/{postId}/re")
    public ResponseEntity<?> rePostMatchingPost(@PathVariable Long postId,
                                                @AuthenticationPrincipal UserDetails userDetails){
        matchingPostService.rePostMatchingPost(postId, userDetails.getUsername());

        return ResponseEntity.noContent().build();
    }

    @PatchMapping("/completion")
    public ResponseEntity<Object> completeMatchingPost(@RequestBody @Valid MatchingPostDto.RequestComplete requestDto){
        matchingPostService.completeMatchingPost(requestDto.getMatchingPostPk());

        return ResponseEntity.noContent().build();
    }

    @GetMapping("/popular/home")
    public ResponseEntity<ResponseHandler<HashMap<Long, MatchingPostDto.ResponseTop15PopularPost>>> getTop15PopularPost(@RequestParam(required = false) SportsType sportsType){
        HashMap<Long, MatchingPostDto.ResponseTop15PopularPost> responseData = matchingPostService.getTop15PopularPost(sportsType);

        return ResponseEntity.ok(
                ResponseHandler.<HashMap<Long, MatchingPostDto.ResponseTop15PopularPost>>builder()
                        .responseMessage("Popular post retrieval successfully")
                        .responseData(responseData)
                        .build()
        );
    }

    @GetMapping("/popular")
    public ResponseEntity<ResponseHandler<HashMap<Long, MatchingPostDto.ResponseTop200PopularPost>>> getTop200PopularPost(@RequestParam(required = false) SportsType sportsType,
                                                                                                                          @AuthenticationPrincipal UserDetails userDetails){
        HashMap<Long, MatchingPostDto.ResponseTop200PopularPost> responseData = matchingPostService.getTop200PopularPost(sportsType, userDetails.getUsername());

        return ResponseEntity.ok(
                ResponseHandler.<HashMap<Long, MatchingPostDto.ResponseTop200PopularPost>>builder()
                        .responseMessage("Popular post retrieval successfully")
                        .responseData(responseData)
                        .build()
        );
    }

    @GetMapping
    public ResponseEntity<ResponseHandler<HashMap<Long, MatchingPostDto.ResponseRecentPost>>> getRecentPost(@RequestParam(required = false) Long nextIdx,
                                                                                                            @AuthenticationPrincipal UserDetails userDetails){
        HashMap<Long, MatchingPostDto.ResponseRecentPost> responseData = matchingPostService.getRecentPost(nextIdx, userDetails.getUsername());

        return ResponseEntity.ok(
                ResponseHandler.<HashMap<Long, MatchingPostDto.ResponseRecentPost>>builder()
                        .responseMessage("Recent post retrieval successfully")
                        .responseData(responseData)
                        .build()
        );
    }

    @GetMapping("/result/count")
    public ResponseEntity<ResponseHandler<Integer>> getSearchPostCnt(@RequestParam @NotBlank String query){
        Integer searchPostCnt = matchingPostService.getSearchPostCnt(query);

        return ResponseEntity.ok(
                ResponseHandler.<Integer>builder()
                        .responseMessage("Search post cnt retrieval successfully")
                        .responseData(searchPostCnt)
                        .build()
        );
    }

    @GetMapping("/result")
    public ResponseEntity<ResponseHandler<HashMap<String, Object>>> getSearchPost(@ModelAttribute @Valid MatchingPostDto.RequestSearchPost searchDto,
                                                                                  @AuthenticationPrincipal UserDetails userDetails){
        HashMap<String, Object> responseData = matchingPostService.getSearchPost(searchDto, userDetails.getUsername());

        return ResponseEntity.ok(
                ResponseHandler.<HashMap<String, Object>>builder()
                        .responseMessage("Search post retrieval successfully")
                        .responseData(responseData)
                        .build()
        );
    }

    @DeleteMapping("/{matchingPostId}")
    public ResponseEntity<ResponseHandler<Object>> deleteMatchingPost(@PathVariable Long matchingPostId,
                                                                      @AuthenticationPrincipal UserDetails userDetails){
        Member member = memberService.findByMemberId(userDetails.getUsername());
        MatchingPost matchingPost = matchingPostService.getMatchingPostByPostId(matchingPostId);
        List<Matching> matchings = matchingService.getMatchingsByMatchingPost(matchingPost);

        if (!matchingPostService.isWriter(matchingPost, member)){
            return ResponseEntity.status(HttpStatus.FORBIDDEN)
                    .body(ResponseHandler.builder()
                            .responseMessage("Only writer can delete post")
                            .build());
        }

        if (!matchingPostService.isDeletable(matchingPost.getTeam(), matchings)){
            return ResponseEntity.status(HttpStatus.FORBIDDEN)
                    .body(ResponseHandler.builder()
                            .responseMessage("Cannot delete matchingPost that have already been matched")
                            .build());
        }

        matchingPostService.deleteMatchingPost(matchingPost, matchings);

        return ResponseEntity.noContent().build();
    }

    @PatchMapping("/{matchingPostId}")
    public ResponseEntity<ResponseHandler<Object>> updateMatchingPostContent(@PathVariable Long matchingPostId,
                                                                             @RequestBody @Valid MatchingPostDto.RequestUpdatePost updatePostDto){
        matchingPostService.updateMatchingPostContent(matchingPostId, updatePostDto.getContent());

        return ResponseEntity.noContent().build();
    }

    @GetMapping("/{matchingPostId}")
    public ResponseEntity<ResponseHandler<MatchingPostDto.ResponseMatchingPostDetail>> getMatchingPostDetail(@PathVariable Long matchingPostId,
                                                                                                             @AuthenticationPrincipal UserDetails userDetails){
        MatchingPostDto.ResponseMatchingPostDetail responseData = matchingPostService.getMatchingPostDetail(matchingPostId, userDetails.getUsername());

        return ResponseEntity.ok(
                ResponseHandler.<MatchingPostDto.ResponseMatchingPostDetail>builder()
                        .responseMessage("Matching post detail retrieval successfully")
                        .responseData(responseData)
                        .build()
                );
    }
}
