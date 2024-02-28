package com.wemingle.core.domain.member.controller;

import com.wemingle.core.domain.member.dto.SetMemberProfileDto;
import com.wemingle.core.domain.member.dto.SignUpDto;
import com.wemingle.core.domain.member.entity.Member;
import com.wemingle.core.domain.member.entity.role.Role;
import com.wemingle.core.domain.member.service.MemberService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@Slf4j
@RestController
@RequestMapping("/member")
@RequiredArgsConstructor
public class MemberController {

    private final MemberService memberService;
    private final BCryptPasswordEncoder bCryptPasswordEncoder;

    @PostMapping("/signup")
    ResponseEntity<?> signUpMember(@RequestBody SignUpDto signUpDto) { //todo 약관 정보 받아야함
        Member member = Member.builder()
                .memberId(signUpDto.getMemberId())
                .password(bCryptPasswordEncoder.encode(signUpDto.getPassword()))
                .nickname(UUID.randomUUID().toString())
                .role(Role.UNVERIFIED_USER)
                .build();
        memberService.saveMember(member);
        return ResponseEntity.ok().build(); //todo token 발급
    }

    @PostMapping("/profile")
    ResponseEntity<ResponseHandler<Object>> setMemberProfile(SetMemberProfileDto setMemberProfileDto,
                                       @AuthenticationPrincipal UserDetails userDetails) {
        Member member = memberService.findMemberByMemberId(userDetails.getUsername());
        memberService.setMemberProfile(member,setMemberProfileDto);
        return ResponseEntity.ok().build(); //todo token 발급
    }

//    @PostMapping("/profile")//todo 온보딩 절차 컨트롤러
}
