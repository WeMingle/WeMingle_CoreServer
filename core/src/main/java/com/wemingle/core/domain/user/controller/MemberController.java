package com.wemingle.core.domain.user.controller;

import com.wemingle.core.domain.user.dto.SignUpDto;
import com.wemingle.core.domain.user.entity.Member;
import com.wemingle.core.domain.user.entity.role.Role;
import com.wemingle.core.domain.user.service.MemberService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
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
    private final BCryptPasswordEncoder bCryptPasswordEncoder;

    @PostMapping("/")
    ResponseEntity<?> signUpMember(@RequestBody SignUpDto signUpDto) {
        Member member = Member.builder()
                .memberName(signUpDto.getMemberName())
                .email(signUpDto.getEmail())
                .dateOfBirth(signUpDto.getDateOfBirth())
                .password(bCryptPasswordEncoder.encode(signUpDto.getPassword()))
                .phoneNumber(signUpDto.getPhoneNumber())
                .nickname("ninini")
                .role(Role.USER)
                .build();
        memberService.saveMember(member);
        return ResponseEntity.ok().build();
    }
}
