package com.wemingle.core.domain.mail.service;

import com.wemingle.core.domain.user.entity.Member;
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

    public UUID getVerificationIdByMemberId(UUID memberId) {
        return memberRepository.findById(memberId)
                .orElseThrow(() -> new EntityNotFoundException(ExceptionMessage.MEMBER_NOT_FOUNT.getExceptionMessage()))
                .getMemberId();
    }
}
