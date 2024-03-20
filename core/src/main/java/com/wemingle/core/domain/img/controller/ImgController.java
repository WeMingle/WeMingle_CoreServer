package com.wemingle.core.domain.img.controller;

import com.wemingle.core.domain.img.service.S3ImgService;
import com.wemingle.core.domain.member.service.MemberService;
import com.wemingle.core.global.responseform.ResponseHandler;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@RestController
@RequestMapping("/img")
@RequiredArgsConstructor
public class ImgController {
    private final MemberService memberService;
    private final S3ImgService s3ImgService;
    @GetMapping("/member/presigned")
    public ResponseEntity<ResponseHandler<Object>> getMemberPreSignUrl(@AuthenticationPrincipal UserDetails userDetails) {
        String profileImgId = memberService.findByMemberId(userDetails.getUsername()).getProfileImgId();
        String memberProfilePreSignedUrl = s3ImgService.getMemberProfilePreSignedUrl(profileImgId);
        return ResponseEntity.ok(ResponseHandler.builder()
                .responseMessage("s3 url issuance complete")
                .responseData(memberProfilePreSignedUrl)
                .build());
    }
}
