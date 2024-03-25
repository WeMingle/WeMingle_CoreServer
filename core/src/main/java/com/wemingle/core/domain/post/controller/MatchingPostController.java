package com.wemingle.core.domain.post.controller;

import com.fasterxml.jackson.databind.node.ObjectNode;
import com.wemingle.core.domain.post.dto.MatchingPostDto;
import com.wemingle.core.domain.post.entity.MatchingPostArea;
import com.wemingle.core.domain.post.entity.abillity.Ability;
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
        return ResponseEntity.ok()
                .body(
                        ResponseHandler.builder()
                                .responseMessage("matching post successfully created")
                                .build()
                );
    }

    @GetMapping("/calendar")
    public ResponseEntity<ResponseHandler<List<ObjectNode>>> getMatchingPostByCalender(@RequestParam(required = false) Long nextIdx,
                                                                                       @RequestParam(required = false) RecruitmentType recruitmentType,
                                                                                       @RequestParam(required = false) Ability ability,
                                                                                       @RequestParam(required = false) Gender gender,
                                                                                       @RequestParam(required = false) RecruiterType recruiterType,
                                                                                       @RequestParam(required = false) List<MatchingPostArea> areaList,
                                                                                       @RequestParam(required = false) LocalDate dateFilter,
                                                                                       @RequestParam(required = false) Boolean excludeExpired){
        List<ObjectNode> getFilteredMatchingPost = matchingPostService.getFilteredMatchingPost(nextIdx, recruitmentType, ability, gender, recruiterType, areaList, dateFilter, excludeExpired);

        return ResponseEntity.ok(
                ResponseHandler.<List<ObjectNode>>builder()
                        .responseMessage("matching posts retrieval successfully")
                        .responseData(getFilteredMatchingPost)
                        .build()
        );
    }
}
