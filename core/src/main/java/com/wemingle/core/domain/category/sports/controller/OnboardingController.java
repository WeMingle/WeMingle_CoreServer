package com.wemingle.core.domain.category.sports.controller;

import com.wemingle.core.domain.category.sports.dto.OnboardDto;
import com.wemingle.core.domain.member.service.MemberService;
import com.wemingle.core.global.responseform.ResponseHandler;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@RestController
@RequestMapping("/onboard")
@RequiredArgsConstructor
public class OnboardingController {
    private final MemberService memberService;

    @PostMapping("/")
    ResponseEntity<ResponseHandler<Object>> setOnboardInfo(@RequestBody OnboardDto.RequestOnboardInfoDto onboardInfoDto,
                                                           @AuthenticationPrincipal UserDetails userDetails) {
        memberService.saveMemberPreferenceSports(userDetails.getUsername(), onboardInfoDto.getSelectedSports());
        return ResponseEntity.ok(
                ResponseHandler.builder()
                        .responseMessage("Onboard update completed")
                        .build()
        );

    }
}
