package com.wemingle.core.domain.member.controller;

import com.wemingle.core.domain.authentication.service.TokenService;
import com.wemingle.core.domain.member.dto.SetMemberProfileDto;
import com.wemingle.core.domain.member.dto.SignUpDto;
import com.wemingle.core.domain.member.entity.Member;
import com.wemingle.core.domain.member.service.MemberService;
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
@RequestMapping("/member")
@RequiredArgsConstructor
public class MemberController {

    private final MemberService memberService;
    private final TokenService tokenService;

    @PostMapping("/signup")
    ResponseEntity<?> signUpMember(@RequestBody SignUpDto.RequestSignUpDto signUpDto) { //todo 약관 정보 받아야함
        memberService.saveMember(signUpDto.of());
        return ResponseEntity.ok().build(); //todo token 발급
    }

    @PostMapping("/profile")
    ResponseEntity<?> setMemberProfile(SetMemberProfileDto setMemberProfileDto,
                                       @AuthenticationPrincipal UserDetails userDetails) {
        Member member = memberService.findByMemberId(userDetails.getUsername());
        memberService.patchMemberProfile(setMemberProfileDto.of());
        return ResponseEntity.ok().build(); //todo token 발급 -> 토큰 서비스에서 사용할 것
    }

//    @PostMapping("/profile")//todo 온보딩 절차 컨트롤러
}
