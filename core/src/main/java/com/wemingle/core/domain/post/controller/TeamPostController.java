package com.wemingle.core.domain.post.controller;

import com.wemingle.core.domain.img.service.S3ImgService;
import com.wemingle.core.domain.post.dto.TeamPostDto;
import com.wemingle.core.domain.post.service.TeamPostService;
import com.wemingle.core.global.responseform.ResponseHandler;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

import java.net.URLDecoder;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;

@RestController
@RequiredArgsConstructor
@Slf4j
public class TeamPostController {
    private final TeamPostService teamPostService;
    private final S3ImgService s3ImgService;

    @GetMapping("/posts/teams")
    public ResponseEntity<ResponseHandler<HashMap<Long, TeamPostDto.ResponseTeamPostsInfoWithMember>>> getTeamPostsInMyTeams(@RequestParam(required = false) Long nextIdx,
                                                                                                                             @AuthenticationPrincipal UserDetails userDetails){
        HashMap<Long, TeamPostDto.ResponseTeamPostsInfoWithMember> responseData = teamPostService.getTeamPostWithMember(nextIdx, userDetails.getUsername());

        return ResponseEntity.ok(
                ResponseHandler.<HashMap<Long, TeamPostDto.ResponseTeamPostsInfoWithMember>>builder()
                        .responseMessage("Team posts retrieval successfully")
                        .responseData(responseData)
                        .build()
        );
    }

    @GetMapping("/posts/teams/{teamId}")
    public ResponseEntity<ResponseHandler<TeamPostDto.ResponseTeamPostsInfoWithTeam>> getTeamPostsWithTeam(@PathVariable Long teamId,
                                                                                                           @RequestParam boolean isNotice,
                                                                                                           @RequestParam(required = false) Long nextIdx,
                                                                                                           @AuthenticationPrincipal UserDetails userDetails) {
        TeamPostDto.ResponseTeamPostsInfoWithTeam responseData = teamPostService.getTeamPostWithTeam(nextIdx, isNotice, teamId, userDetails.getUsername());

        return ResponseEntity.ok(
                ResponseHandler.<TeamPostDto.ResponseTeamPostsInfoWithTeam>builder()
                        .responseMessage("Team posts retrieval successfully")
                        .responseData(responseData)
                        .build()
        );
    }

    @PostMapping("/posts/teams")
    public ResponseEntity<Object> saveTeamPost(@RequestBody @Valid TeamPostDto.RequestTeamPostSave postSaveDto,
                                               @AuthenticationPrincipal UserDetails userDetails){
        s3ImgService.verifyImgsExistInTeamPostS3(postSaveDto.getImgIds());
        teamPostService.saveTeamPost(postSaveDto, userDetails.getUsername());

        return ResponseEntity.noContent().build();
    }

    @GetMapping("/posts/teams/result")
    public ResponseEntity<ResponseHandler<HashMap<Long, TeamPostDto.ResponseSearchTeamPost>>> getSearchTeamPosts(@RequestParam(required = false) Long nextIdx,
                                                                                                                 @RequestParam @NotBlank(message = "검색어는 최소 한글자입니다.") String query,
                                                                                                                 @RequestParam Long teamPk,
                                                                                                                 @AuthenticationPrincipal UserDetails userDetails){
         String searchKeyword = URLDecoder.decode(query, StandardCharsets.UTF_8);
         HashMap<Long, TeamPostDto.ResponseSearchTeamPost> responseData = teamPostService.getSearchTeamPost(nextIdx, teamPk, searchKeyword, userDetails.getUsername());

         return ResponseEntity.ok(
                ResponseHandler.<HashMap<Long, TeamPostDto.ResponseSearchTeamPost>>builder()
                        .responseMessage("Team posts retrieval successfully")
                        .responseData(responseData)
                        .build()
         );
    }

    @PostMapping("/posts/{teamPostId}/teams/like")
    public ResponseEntity<Object> savePostLike(@PathVariable Long teamPostId,
                                               @AuthenticationPrincipal UserDetails userDetails){
        teamPostService.savePostLike(teamPostId, userDetails.getUsername());

        return ResponseEntity.noContent().build();
    }

    @DeleteMapping("/posts/{teamPostId}/teams/like")
    public ResponseEntity<Object> deletePostLike(@PathVariable Long teamPostId,
                                                 @AuthenticationPrincipal UserDetails userDetails){
        teamPostService.deletePostLike(teamPostId, userDetails.getUsername());

        return ResponseEntity.noContent().build();
    }

    @GetMapping("/posts/{teamPostId}/teams/detail")
    public ResponseEntity<ResponseHandler<TeamPostDto.ResponseTeamPostDetail>> getTeamPostDetail(@PathVariable Long teamPostId,
                                                                                                 @AuthenticationPrincipal UserDetails userDetails){
        TeamPostDto.ResponseTeamPostDetail responseData = teamPostService.getTeamPostDetail(teamPostId, userDetails.getUsername());

        return ResponseEntity.ok(
                ResponseHandler.<TeamPostDto.ResponseTeamPostDetail>builder()
                        .responseMessage("Team post retrieval successfully")
                        .responseData(responseData)
                        .build()
        );
    }
}
