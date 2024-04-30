package com.wemingle.core.domain.img.controller;

import com.wemingle.core.domain.img.service.S3ImgService;
import com.wemingle.core.domain.member.service.MemberService;
import com.wemingle.core.domain.team.service.TeamMemberService;
import com.wemingle.core.global.responseform.ResponseHandler;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

import java.util.*;

@Slf4j
@RestController
@RequestMapping("/img")
@RequiredArgsConstructor
public class ImgController {
    private final MemberService memberService;
    private final S3ImgService s3ImgService;
    private final TeamMemberService teamMemberService;

    private static final int MAX_IMG_COUNT = 5;
    @GetMapping("/member/profile/upload")
    public ResponseEntity<ResponseHandler<Object>> getMemberProfilePicUploadPreSignUrl(@AuthenticationPrincipal UserDetails userDetails) {
        UUID profileImgId = memberService.findByMemberId(userDetails.getUsername()).getProfileImgId();
        String memberProfilePreSignedUrl = s3ImgService.setMemberProfilePreSignedUrl(profileImgId);
        return ResponseEntity.ok(ResponseHandler.builder()
                .responseMessage("s3 url issuance complete")
                .responseData(memberProfilePreSignedUrl)
                .build());
    }

    @GetMapping("/member/profile/{id}")
    public ResponseEntity<ResponseHandler<Object>> getMemberProfilePicRetrievePreSignUrl(@PathVariable("id") UUID picUUID) {
        String memberProfilePreSignedUrl = s3ImgService.getMemberProfilePicUrl(picUUID);
        return ResponseEntity.ok(ResponseHandler.builder()
                .responseMessage("s3 url issuance complete")
                .responseData(memberProfilePreSignedUrl)
                .build());
    }

    @GetMapping("/team/profile/upload/{teamImgUUID}")
    public ResponseEntity<ResponseHandler<Object>> getTeamProfilePicUploadPreSignUrl(@PathVariable("teamImgUUID") UUID teamImgUUID, @AuthenticationPrincipal UserDetails userDetails) {
        String groupProfilePreSignedUrl = s3ImgService.setGroupProfilePreSignedUrl(teamImgUUID);
        return ResponseEntity.ok(ResponseHandler.builder()
                .responseMessage("s3 url issuance complete")
                .responseData(groupProfilePreSignedUrl)
                .build());
    }

    @GetMapping("/post/team/upload")
    public ResponseEntity<ResponseHandler<?>> getTeamPostPicUploadPreSignUrl(@RequestParam List<String> extensions) {
        if (extensions.size() > MAX_IMG_COUNT){
            return ResponseEntity.ok(
                    ResponseHandler.builder()
                    .responseMessage("Up to 5 images can upload")
                    .build());
        }

        HashMap<String, ArrayList<String>> teamPostPreSignedUrls = s3ImgService.setTeamPostPreSignedUrl(extensions);

        return ResponseEntity.ok(ResponseHandler.<HashMap<String, ArrayList<String>>>builder()
                .responseMessage("s3 url issuance complete")
                .responseData(teamPostPreSignedUrls)
                .build());
    }

    @GetMapping("/members/team")
    public ResponseEntity<ResponseHandler<List<String>>> getTeamMembersRetrievePreSignedUrl(@RequestParam List<Long> teamMembersPk){
        List<String> responseData = teamMemberService.getTeamMembersImgUrl(teamMembersPk);

        return ResponseEntity.ok(ResponseHandler.<List<String>>builder()
                .responseMessage("s3 url issuance complete")
                .responseData(responseData)
                .build());
    }
}
