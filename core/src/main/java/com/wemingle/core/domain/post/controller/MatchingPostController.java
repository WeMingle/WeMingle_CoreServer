package com.wemingle.core.domain.post.controller;

import com.wemingle.core.domain.post.dto.MatchingPostDto;
import com.wemingle.core.domain.post.entity.abillity.Ability;
import com.wemingle.core.domain.post.entity.area.AreaName;
import com.wemingle.core.domain.post.entity.gender.Gender;
import com.wemingle.core.domain.post.entity.recruitertype.RecruiterType;
import com.wemingle.core.domain.post.service.MatchingPostService;
import com.wemingle.core.domain.team.entity.recruitmenttype.RecruitmentType;
import com.wemingle.core.global.responseform.ResponseHandler;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/post/match")
public class MatchingPostController {
    private final MatchingPostService matchingPostService;
    @PostMapping
    ResponseEntity<ResponseHandler<Object>> createMatchingPost(@RequestBody MatchingPostDto.CreateMatchingPostDto matchingPostDto,
                                                               @AuthenticationPrincipal UserDetails userDetails) {
        matchingPostService.createMatchingPost(matchingPostDto, userDetails.getUsername());
        return ResponseEntity.ok() //todo 글 상세페이지 조회 api 구현 후 201로 변경하고 url 함께 반환
                .body(
                        ResponseHandler.builder()
                                .responseMessage("matching post successfully created")
                                .build()
                );
    }

    @GetMapping("/calendar")
    public ResponseEntity<ResponseHandler<Object>> getMatchingPostByCalender(@RequestParam(required = false) Long nextIdx,
                                                                                       @RequestParam(required = false) RecruitmentType recruitmentType,
                                                                                       @RequestParam(required = false) Ability ability,
                                                                                       @RequestParam(required = false) Gender gender,
                                                                                       @RequestParam(required = false) RecruiterType recruiterType,
                                                                                       @RequestParam(required = false) List<AreaName> areaList,
                                                                                       @RequestParam(required = false) LocalDate dateFilter,
                                                                                       @RequestParam(required = false) Boolean excludeExpired,
                                                                                       @AuthenticationPrincipal UserDetails userDetails){
        HashMap<Long, Object> getFilteredMatchingPost = matchingPostService.getFilteredMatchingPost(userDetails.getUsername(), nextIdx, recruitmentType, ability, gender, recruiterType, areaList, dateFilter, excludeExpired);

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
}
