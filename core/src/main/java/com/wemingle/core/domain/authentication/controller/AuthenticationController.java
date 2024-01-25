package com.wemingle.core.domain.authentication.controller;

import com.wemingle.core.domain.authentication.dto.TokenDto;
import com.wemingle.core.domain.user.service.MemberService;
import com.wemingle.core.global.config.jwt.TokenProvider;
import com.wemingle.core.global.responseform.ResponseHandler;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@Slf4j
public class AuthenticationController {
    private final TokenProvider tokenProvider;
    private final MemberService memberService;


    @PatchMapping
    public ResponseEntity<ResponseHandler<?>> patchToken(@RequestBody TokenDto.RequestTokenDto requestTokenDto,
                                                         @AuthenticationPrincipal UserDetails userDetails){
        String memberEmail = userDetails.getUsername();

    }
}
