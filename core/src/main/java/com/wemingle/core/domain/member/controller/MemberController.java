package com.wemingle.core.domain.member.controller;

import com.wemingle.core.domain.authentication.dto.TokenDto;
import com.wemingle.core.domain.authentication.service.TokenService;
import com.wemingle.core.domain.member.dto.SetMemberProfileDto;
import com.wemingle.core.domain.member.dto.SignUpDto;
import com.wemingle.core.domain.member.entity.Member;
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
@RequestMapping("/member")
@RequiredArgsConstructor
public class MemberController {

    private final MemberService memberService;
    private final TokenService tokenService;

    @PostMapping("/signup")
    ResponseEntity<?> signUpMember(@RequestBody SignUpDto.RequestSignUpDto signUpDto) { //todo 약관 정보 받아야함
        memberService.saveMember(signUpDto.of());
        TokenDto.ResponseTokenDto unVerifiedUserTokens = tokenService.getUnVerifiedUserTokens(signUpDto.getMemberId());
        return ResponseEntity.ok().body(
                ResponseHandler.builder()
                        .responseMessage("Token issuance completed")
                        .responseData(unVerifiedUserTokens)
                        .build()
        );
    }

    @PostMapping("/profile")
    ResponseEntity<?> setMemberProfile(SetMemberProfileDto setMemberProfileDto,
                                       @AuthenticationPrincipal UserDetails userDetails) {
        memberService.patchMemberProfile(setMemberProfileDto.of());//todo memberid 같이 넘기는 걸로 업데이트되면 추후 수정
        return ResponseEntity.ok().body(
                ResponseHandler.builder()
                        .responseMessage("Token issuance completed")
                        .responseData(unVerifiedUserTokens)
                        .build()
        ); //todo token 발급 -> 토큰 서비스에서 사용할 것
    }

//    @PostMapping("/profile")//todo 온보딩 절차 컨트롤러
}
