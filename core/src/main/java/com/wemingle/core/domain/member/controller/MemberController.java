package com.wemingle.core.domain.member.controller;

import com.wemingle.core.domain.authentication.dto.TokenDto;
import com.wemingle.core.domain.authentication.service.TokenService;
import com.wemingle.core.domain.member.dto.SetMemberProfileDto;
import com.wemingle.core.domain.member.dto.SignUpDto;
import com.wemingle.core.domain.member.service.MemberService;
import com.wemingle.core.domain.member.vo.SignupVo;
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
    ResponseEntity<?> signUpMember(@RequestBody SignUpDto.RequestSignUpDto signUpDto) {
        TokenDto.ResponseTokenDto unVerifiedUserTokens = tokenService.getUnVerifiedUserTokens(signUpDto.getMemberId());
        SignupVo.SaveMemberVo saveMemberVo = signUpDto.of();
        saveMemberVo.setRefreshToken(unVerifiedUserTokens.getRefreshToken());
        memberService.saveMember(saveMemberVo);
        return ResponseEntity.ok().body(
                ResponseHandler.builder()
                        .responseMessage("Token issuance completed")
                        .responseData(unVerifiedUserTokens)
                        .build()
        );
    }

    @PostMapping("/profile")
    ResponseEntity<ResponseHandler<Object>> setMemberProfile(SetMemberProfileDto setMemberProfileDto,
                                       @AuthenticationPrincipal UserDetails userDetails) {
        String memberId = userDetails.getUsername();
        SignupVo.PatchMemberProfileVo patchMemberProfileVo = setMemberProfileDto.of();
        patchMemberProfileVo.setMemberId(memberId);
        memberService.patchMemberProfile(patchMemberProfileVo);


        return ResponseEntity.ok().body(
                ResponseHandler.builder()
                        .responseMessage("Token issuance completed")
                        .responseData(null)
                        .build()
        );
    }


}
