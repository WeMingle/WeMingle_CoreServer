package com.wemingle.core.domain.post.controller;

import com.wemingle.core.domain.post.dto.TeamPostDto;
import com.wemingle.core.domain.post.service.TeamPostService;
import com.wemingle.core.global.responseform.ResponseHandler;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;

@RestController
@RequiredArgsConstructor
@RequestMapping("/post/team")
public class TeamPostController {
    private final TeamPostService teamPostService;

    @GetMapping
    public ResponseEntity<ResponseHandler<HashMap<Long, TeamPostDto.ResponseTeamPostsInfoWithMember>>> getTeamPostsWithMember(@RequestParam(required = false) Long nextIdx,
                                                                                                                              @AuthenticationPrincipal UserDetails userDetails){
        HashMap<Long, TeamPostDto.ResponseTeamPostsInfoWithMember> responseData = teamPostService.getTeamPostWithMember(nextIdx, userDetails.getUsername());

        return ResponseEntity.ok(
                ResponseHandler.<HashMap<Long, TeamPostDto.ResponseTeamPostsInfoWithMember>>builder()
                        .responseMessage("Team posts retrieval successfully")
                        .responseData(responseData)
                        .build()
        );
    }

    @GetMapping("/{teamPk}")
    public ResponseEntity<ResponseHandler<TeamPostDto.ResponseTeamPostsInfoWithTeam>> getTeamPostsWithMember(@PathVariable Long teamPk,
                                                                                                                              @RequestParam boolean isNotice,
                                                                                                                              @RequestParam(required = false) Long nextIdx,
                                                                                                                              @AuthenticationPrincipal UserDetails userDetails){
        TeamPostDto.ResponseTeamPostsInfoWithTeam responseData = teamPostService.getTeamPostWithTeam(nextIdx, isNotice, teamPk, userDetails.getUsername());

        return ResponseEntity.ok(
                ResponseHandler.<TeamPostDto.ResponseTeamPostsInfoWithTeam>builder()
                        .responseMessage("Team posts retrieval successfully")
                        .responseData(responseData)
                        .build()
        );
    }
}
