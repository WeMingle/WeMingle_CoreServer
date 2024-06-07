package com.wemingle.core.domain.img.controller;

import com.wemingle.core.domain.img.service.S3ImgService;
import com.wemingle.core.domain.member.service.MemberService;
import com.wemingle.core.domain.team.entity.Team;
import com.wemingle.core.domain.team.service.TeamMemberService;
import com.wemingle.core.domain.team.service.TeamService;
import com.wemingle.core.global.responseform.ResponseHandler;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@Slf4j
@RestController
@RequestMapping("/images")
@RequiredArgsConstructor
public class ImgController {
    private final MemberService memberService;
    private final S3ImgService s3ImgService;
    private final TeamMemberService teamMemberService;
    private final TeamService teamService;

    private static final int MAX_IMG_COUNT = 5;
    @GetMapping("/members/profile/upload/{extension}")
    public ResponseEntity<ResponseHandler<Object>> getMemberProfilePicUploadPreSignUrl(@PathVariable("extension") String extension, @AuthenticationPrincipal UserDetails userDetails) {
        if (!s3ImgService.isAvailableExtension(extension)) {
            return ResponseEntity.badRequest().body(ResponseHandler.builder().responseMessage("extension is not allowed").build());
        }

        UUID profileImgId = memberService.findByMemberId(userDetails.getUsername()).getProfileImgId();
        String memberProfilePreSignedUrl = s3ImgService.setMemberProfilePreSignedUrl(profileImgId);
        return ResponseEntity.ok(ResponseHandler.builder()
                .responseMessage("s3 url issuance complete")
                .responseData(memberProfilePreSignedUrl)
                .build());
    }

    @GetMapping("/members/profile/{id}")
    public ResponseEntity<ResponseHandler<Object>> getMemberProfilePicRetrievePreSignUrl(@PathVariable("id") UUID picUUID) {
        String memberProfilePreSignedUrl = s3ImgService.getMemberProfilePicUrl(picUUID);
        return ResponseEntity.ok(ResponseHandler.builder()
                .responseMessage("s3 url issuance complete")
                .responseData(memberProfilePreSignedUrl)
                .build());
    }

    @GetMapping("/teams/profile/upload/{teamImgUUID}/{extension}")
    public ResponseEntity<ResponseHandler<Object>> getTeamProfilePicUploadPreSignUrl(@PathVariable("teamImgUUID") UUID teamImgUUID, @PathVariable("extension") String extension, @AuthenticationPrincipal UserDetails userDetails) {
        if (!s3ImgService.isAvailableExtension(extension)) {
            return ResponseEntity.badRequest().body(ResponseHandler.builder().responseMessage("extension is not allowed").build());
        }
        String groupProfilePreSignedUrl = s3ImgService.setGroupProfilePreSignedUrl(teamImgUUID);
        return ResponseEntity.ok(ResponseHandler.builder()
                .responseMessage("s3 url issuance complete")
                .responseData(groupProfilePreSignedUrl)
                .build());
    }

    @GetMapping("/posts/teams/upload")
    public ResponseEntity<ResponseHandler<?>> getTeamPostPicUploadPreSignUrl(@RequestParam List<String> extensions) {
        int imgCnt = extensions.size();

        if (imgCnt > MAX_IMG_COUNT){
            return ResponseEntity.badRequest().body(
                    ResponseHandler.builder()
                    .responseMessage("Up to 5 images can upload")
                    .build());
        }

        if (!s3ImgService.isAvailableExtensions(extensions)) {
            return ResponseEntity.badRequest()
                    .body(ResponseHandler.builder()
                            .responseMessage("extension is not allowed")
                            .build());
        }

        List<String> teamPostPreSignedUrls = s3ImgService.setTeamPostPreSignedUrl(imgCnt);

        return ResponseEntity.ok(ResponseHandler.<List<String>>builder()
                .responseMessage("s3 url issuance complete")
                .responseData(teamPostPreSignedUrls)
                .build());
    }

    @GetMapping("/members/teams")
    public ResponseEntity<ResponseHandler<List<String>>> getTeamMembersRetrievePreSignedUrl(@RequestParam List<Long> teamMembersId){
        List<String> responseData = teamMemberService.getTeamMembersImgUrl(teamMembersId);

        return ResponseEntity.ok(ResponseHandler.<List<String>>builder()
                .responseMessage("s3 url issuance complete")
                .responseData(responseData)
                .build());
    }

    @GetMapping("/teams/request/profile/upload/{teamImgUUID}/{extension}")
    public ResponseEntity<ResponseHandler<Object>> getTeamRequestProfilePicUploadPreSignUrl(@PathVariable("teamImgUUID")UUID teamImgUUID,
                                                                                           @PathVariable("extension")String extension) {
        if (!s3ImgService.isAvailableExtension(extension)) {
            return ResponseEntity.badRequest().body(ResponseHandler.builder().responseMessage("extension is not allowed").build());
        }

        return ResponseEntity.ok(
                ResponseHandler.builder()
                .responseMessage("s3 url issuance complete")
                .responseData(s3ImgService.setTeamMemberProfilePreSignedUrl(teamImgUUID))
                .build());
    }

    @GetMapping("/teams/background/upload/{teamId}/{extension}")
    public ResponseEntity<ResponseHandler<Object>> getTeamBackgroundUploadPreSignUrl(@PathVariable("teamId") Long teamId,
                                                                                     @PathVariable("extension") String extension) {
        if (!s3ImgService.isAvailableExtension(extension)) {
            return ResponseEntity.badRequest().body(ResponseHandler.builder().responseMessage("extension is not allowed").build());
        }

        Team team = teamService.findById(teamId);

        return ResponseEntity.ok(
                ResponseHandler.builder()
                        .responseMessage("s3 url issuance complete")
                        .responseData(s3ImgService.setTeamBackgroundPreSignedUrl(team.getBackgroundImgId()))
                        .build());
    }
}
