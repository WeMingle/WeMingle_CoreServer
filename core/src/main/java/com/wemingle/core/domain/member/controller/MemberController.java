package com.wemingle.core.domain.member.controller;

import com.wemingle.core.domain.authentication.dto.TokenDto;
import com.wemingle.core.domain.authentication.service.TokenService;
import com.wemingle.core.domain.member.dto.*;
import com.wemingle.core.domain.member.entity.signupplatform.SignupPlatform;
import com.wemingle.core.domain.member.service.MemberService;
import com.wemingle.core.domain.member.vo.SignupVo;
import com.wemingle.core.global.responseform.ResponseHandler;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.web.bind.annotation.*;

@Slf4j
@RestController
@RequestMapping("/member")
@RequiredArgsConstructor
public class MemberController {

    private final MemberService memberService;
    private final TokenService tokenService;
    private final BCryptPasswordEncoder passwordEncoder;

    @PostMapping("/signin")
    ResponseEntity<ResponseHandler<Object>> signInMember(@RequestBody SignUpDto.RequestSignInDto signInDto) {
        boolean isRegisteredMember = memberService.isRegisteredMember(signInDto.getMemberId(), signInDto.getSignupPlatform());
        if (!isRegisteredMember) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(ResponseHandler.builder()
                            .responseMessage("Member not found")
                            .build());
        }
        if (!memberService.isMatchesPassword(signInDto.getMemberId(), signInDto.getPassword())) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(ResponseHandler.builder()
                            .responseMessage("Passwords do not match")
                            .build());
        }
        TokenDto.ResponseTokenDto tokensForRegisteredMember = tokenService.getTokensForRegisteredMember(signInDto.getMemberId());
        return ResponseEntity.ok(ResponseHandler.builder()
                .responseMessage("Token reissuance complete")
                .responseData(tokensForRegisteredMember)
                .build());
    }

    @PostMapping("/signup")
    ResponseEntity<ResponseHandler<Object>> signUpMember(@RequestBody SignUpDto.RequestSignUpDto signUpDto) {
        boolean registeredMember = memberService.isRegisteredMember(signUpDto.getMemberId(), signUpDto.getSignupPlatform());
        if (registeredMember) {
            SignupPlatform registeredPlatformByMember = memberService.findRegisteredPlatformByMember(signUpDto.getMemberId());
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(ResponseHandler.builder()
                            .responseMessage("Member is registered")
                            .responseData(registeredPlatformByMember.getPlatformType())
                            .build());
        }
        TokenDto.ResponseTokenDto unVerifiedUserTokens = tokenService.getUnVerifiedUserTokens(signUpDto.getMemberId());
        SignupVo.SaveMemberVo saveMemberVo = signUpDto.of();
        saveMemberVo.setRefreshToken(unVerifiedUserTokens.getRefreshToken());
        memberService.saveMember(saveMemberVo);
        return ResponseEntity.ok().body(
                ResponseHandler.builder()
                        .responseMessage("Temporary token issuance completed")
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
                        .responseMessage("Profile update completed")
                        .build()
        );
    }

    @GetMapping("/info")
    ResponseEntity<ResponseHandler<Object>> getMyInfo(@AuthenticationPrincipal UserDetails userDetails) {
        MemberInfoDto memberInfo = memberService.getMemberInfo(userDetails.getUsername());

        return ResponseEntity.ok().body(ResponseHandler.builder()
                .responseMessage("member info retrieval successfully")
                .responseData(memberInfo).build()
        );
    }

    @PatchMapping("/info")
    ResponseEntity<ResponseHandler<Object>> setMyInfo(@RequestBody MemberInfoDto memberInfoDto, @AuthenticationPrincipal UserDetails userDetails) {
        memberService.setMemberInfo(userDetails.getUsername(), memberInfoDto);

        return ResponseEntity.ok().body(ResponseHandler.builder()
                .responseMessage("member info update successfully")
                .build()
        );
    }

    @GetMapping("/authentication")
    ResponseEntity<ResponseHandler<Object>> getAuthenticationInfo(@AuthenticationPrincipal UserDetails userDetails) {
        MemberAuthenticationInfoDto memberAuthenticationInfo = memberService.getMemberAuthenticationInfo(userDetails.getUsername());

        return ResponseEntity.ok().body(ResponseHandler.builder()
                .responseMessage("member authentication info retrieval successfully")
                .responseData(memberAuthenticationInfo).build()
        );
    }

    @GetMapping
    ResponseEntity<ResponseHandler<MemberDto.ResponseMemberInfo>> getMemberByNickname(@RequestParam(required = false) Long nextIdx,
                                                                                      @RequestParam @NotBlank @NotNull @NotEmpty String nickname){
        MemberDto.ResponseMemberInfo responseData = memberService.getMemberByNickname(nextIdx, nickname);

        return ResponseEntity.ok(
                ResponseHandler.<MemberDto.ResponseMemberInfo>builder()
                        .responseMessage("Member retrieval successfully")
                        .responseData(responseData)
                        .build()
        );
    }

}
