package com.wemingle.core.domain.memberunivemail.service;

import com.wemingle.core.domain.member.repository.MemberRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class MailVerificationService {
    private final MemberRepository memberRepository;

//    public boolean isPresentMemberIdByVerificationId(UUID verificationId) {
//        return memberRepository.findById(verificationId).isPresent();
//    }
}
