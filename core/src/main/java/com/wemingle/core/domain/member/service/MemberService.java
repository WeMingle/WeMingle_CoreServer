package com.wemingle.core.domain.user.service;

import com.wemingle.core.domain.mail.service.MemberSignUpEvent;
import com.wemingle.core.domain.nickname.service.NicknameService;
import com.wemingle.core.domain.univ.service.UnivCertificationService;
import com.wemingle.core.domain.user.entity.Member;
import com.wemingle.core.domain.user.repository.MemberRepository;
import com.wemingle.core.global.exceptionmessage.ExceptionMessage;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.NoSuchElementException;
import java.util.StringTokenizer;

import static com.wemingle.core.global.exceptionmessage.ExceptionMessage.*;

@RequiredArgsConstructor
@Service
@Transactional(readOnly = true)
public class MemberService {
    private final MemberRepository memberRepository;
    private final ApplicationEventPublisher applicationEventPublisher;
    private final UnivCertificationService univCertificationService;
    private final NicknameService nicknameService;

    @Transactional
    public void saveMember(Member member) {
        String domainInMailAddress = univCertificationService.getDomainInMailAddress(member.getEmail());
        boolean isAvailableDomain = univCertificationService.validUnivDomain(domainInMailAddress);
        if (!nicknameService.isAvailableNickname(member.getNickname())) {
            throw new IllegalStateException(UNAVAILABLE_NICKNAME.getExceptionMessage());
        }
        if (!isAvailableDomain) {
            throw new NoSuchElementException(UNIV_DOMAIN_NOT_FOUND.getExceptionMessage());
        }
        memberRepository.save(member);
        applicationEventPublisher.publishEvent(new MemberSignUpEvent(member));
    }

    public Member findByEmail(String memberEmail){
        return memberRepository.findByEmail(memberEmail)
                .orElseThrow(() -> new EntityNotFoundException(MEMBER_NOT_FOUNT.getExceptionMessage()));

    }

    public Member findByRefreshToken(String refreshToken){
        return memberRepository.findByRefreshToken(refreshToken)
                .orElseThrow(() -> new EntityNotFoundException(MEMBER_NOT_FOUNT.getExceptionMessage()));
    }
}
