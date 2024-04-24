package com.wemingle.core.domain.img.controller;

import com.wemingle.core.domain.img.service.S3ImgService;
import com.wemingle.core.domain.member.service.MemberService;
import com.wemingle.core.domain.team.service.TeamService;
import com.wemingle.core.global.responseform.ResponseHandler;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.UUID;

@Slf4j
@RestController
@RequestMapping("/img")
@RequiredArgsConstructor
public class ImgController {
    private final MemberService memberService;
    private final S3ImgService s3ImgService;
    private final TeamService teamService;
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
}
