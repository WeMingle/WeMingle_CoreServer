package com.wemingle.core.domain.memberunivemail.controller;

import com.wemingle.core.domain.authentication.dto.TokenDto;
import com.wemingle.core.domain.authentication.service.TokenService;
import com.wemingle.core.domain.member.entity.Member;
import com.wemingle.core.domain.member.entity.signupplatform.SignupPlatform;
import com.wemingle.core.domain.member.service.MemberService;
import com.wemingle.core.domain.memberunivemail.dto.MailDto;
import com.wemingle.core.domain.memberunivemail.service.MailVerificationService;
import com.wemingle.core.domain.univ.entity.UnivEntity;
import com.wemingle.core.domain.univ.service.UnivCertificationService;
import com.wemingle.core.global.exceptionmessage.ExceptionMessage;
import com.wemingle.core.global.responseform.ResponseHandler;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

@RequiredArgsConstructor
@RestController
@RequestMapping("/mail")
public class MailController {
    private final MemberService memberService;
    private final MailVerificationService mailVerificationService;
    private final UnivCertificationService univCertificationService;
    private final TokenService tokenService;

    private final static String IS_REGISTERED_PLATFORM_SUFFIX = "로 이미 가입된 학교 이메일입니다";

    @PostMapping()
    public ResponseEntity<?> sendMail(@RequestBody MailDto.RequestSendMailDto requestSendMailDto,
                                      @AuthenticationPrincipal UserDetails userDetails){
        String memberId = userDetails.getUsername();
        Member findMember = memberService.findByMemberId(memberId);

        String requestUnivEmail = requestSendMailDto.getUnivEmail();
        String registeredPlatform = mailVerificationService.getRegisteredPlatform(requestUnivEmail);
        if (isRegisteredUnivEmail(registeredPlatform)){
            SignupPlatform signupPlatform = SignupPlatform.valueOf(registeredPlatform);
            return ResponseEntity
                    .status(HttpStatus.CONFLICT)
                    .body(ResponseHandler.<MailDto.ResponseRegisteredPlatformDto>builder()
                            .responseMessage(signupPlatform.getPlatformType() + IS_REGISTERED_PLATFORM_SUFFIX)
                            .responseData(new MailDto.ResponseRegisteredPlatformDto(signupPlatform.getPlatformType()))
                            .build());
        }

        mailVerificationService.sendVerificationMail(requestUnivEmail, findMember);

        return ResponseEntity.noContent().build();
    }

    @GetMapping("/verification")
    ResponseEntity<ResponseHandler<Object>> getVerifyCode(@RequestBody MailDto.RequestSendMailDto mailDto, @AuthenticationPrincipal UserDetails userDetails) {
        Member byMemberId = memberService.findByMemberId(userDetails.getUsername());
        mailVerificationService.sendVerificationMail(mailDto.getUnivEmail(),byMemberId);
        return ResponseEntity.noContent().build();
    }

    @PatchMapping
    ResponseEntity<ResponseHandler<Object>> changeUnivEmail(@RequestBody MailDto.RequestVerifyCodeDto requestVerifyCodeDto, @AuthenticationPrincipal UserDetails userDetails) {
        Member byMemberId = memberService.findByMemberId(userDetails.getUsername());
        mailVerificationService.saveChangedVerificationMailAddress(byMemberId, requestVerifyCodeDto.getUnivEmail(), requestVerifyCodeDto.getVerificationCode());
        return ResponseEntity.noContent().build();
    }

    private boolean isRegisteredUnivEmail(String registeredPlatform){
        return !registeredPlatform.equals(ExceptionMessage.UNREGISTERED_MEMBER.toString());
    }

    @PostMapping("/verify")
    public ResponseEntity<?> verifyCode(@RequestBody MailDto.RequestVerifyCodeDto requestVerifyCodeDto,
                                        @AuthenticationPrincipal UserDetails userDetails){
        String memberId = userDetails.getUsername();
        Member findMember = memberService.findByMemberId(memberId);

        if(!mailVerificationService.validVerificationCode(memberId, requestVerifyCodeDto.getVerificationCode())){
            return ResponseEntity
                    .status(HttpStatus.BAD_REQUEST)
                    .body(ResponseHandler.builder()
                            .responseMessage("코드가 일치하지않습니다.")
                            .responseData(null)
                            .build());
        }

        String univDomain = univCertificationService.getDomainInMailAddress(requestVerifyCodeDto.getUnivEmail());
        UnivEntity univEntity = univCertificationService.findByDomain(univDomain);
        mailVerificationService.saveVerifiedUniversityEmail(findMember, univEntity);
        TokenDto.ResponseTokenDto renewalToken = tokenService.getTokensAndConvertToAuthenticationUser(findMember);

        return ResponseEntity.ok(
                ResponseHandler.<TokenDto.ResponseTokenDto>builder()
                        .responseMessage("인증이 완료되어 토큰을 재발급합니다.")
                        .responseData(renewalToken)
                        .build());
    }
}