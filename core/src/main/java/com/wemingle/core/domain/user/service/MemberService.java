package com.wemingle.core.domain.user.service;

import com.wemingle.core.domain.mail.service.MemberSignUpEvent;
import com.wemingle.core.domain.univ.service.UnivCertificationService;
import com.wemingle.core.domain.user.entity.Member;
import com.wemingle.core.domain.user.repository.MemberRepository;
import com.wemingle.core.global.exceptionmessage.ExceptionMessage;
import lombok.RequiredArgsConstructor;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.NoSuchElementException;
import java.util.StringTokenizer;

@RequiredArgsConstructor
@Service
public class MemberService {
    private final MemberRepository memberRepository;
    private final ApplicationEventPublisher applicationEventPublisher;
    private final UnivCertificationService univCertificationService;

    @Transactional
    public void saveMember(Member member) {
        String domainInMailAddress = univCertificationService.getDomainInMailAddress(member.getEmail());
        boolean isAvailableDomain = univCertificationService.validUnivDomain(domainInMailAddress);
        if (!isAvailableDomain) {
            throw new NoSuchElementException(ExceptionMessage.UNIV_DOMAIN_NOT_FOUND.getExceptionMessage());
        }
        memberRepository.save(member);
        applicationEventPublisher.publishEvent(new MemberSignUpEvent(member));
    }
}
