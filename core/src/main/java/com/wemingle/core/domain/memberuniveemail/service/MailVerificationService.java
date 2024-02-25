package com.wemingle.core.domain.memberuniveemail.service;

import com.wemingle.core.domain.member.repository.MemberRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
@RequiredArgsConstructor
public class MailVerificationService {
    private final MemberRepository memberRepository;

    public boolean isPresentMemberIdByVerificationId(UUID verificationId) {
        return memberRepository.findById(verificationId).isPresent();
    }
}
