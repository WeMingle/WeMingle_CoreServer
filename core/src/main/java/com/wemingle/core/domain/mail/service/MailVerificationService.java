package com.wemingle.core.domain.mail.service;

import com.wemingle.core.domain.user.repository.MemberRepository;
import com.wemingle.core.global.exceptionmessage.ExceptionMessage;
import jakarta.persistence.EntityNotFoundException;
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
