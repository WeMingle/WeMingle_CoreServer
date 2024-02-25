package com.wemingle.core.domain.member.service;

import com.wemingle.core.domain.nickname.service.NicknameService;
import com.wemingle.core.domain.univ.service.UnivCertificationService;
import com.wemingle.core.domain.member.entity.Member;
import com.wemingle.core.domain.member.repository.MemberRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@RequiredArgsConstructor
@Service
public class MemberService {
    private final MemberRepository memberRepository;
    private final ApplicationEventPublisher applicationEventPublisher;
    private final UnivCertificationService univCertificationService;
    private final NicknameService nicknameService;

    @Transactional
    public void saveMember(Member member) {
//        String domainInMailAddress = univCertificationService.getDomainInMailAddress(member.getEmail());
//        boolean isAvailableDomain = univCertificationService.validUnivDomain(domainInMailAddress);
//        if (isAvailableEmail(member.getEmail())) {//todo 어떤 계정으로 회원가입되었는지 확인 후 리턴할 것(예: 카카오로 이미 가입된 학교 이메일입니다)
//            throw new IllegalStateException(ExceptionMessage.UNAVAILABLE_EMAIL.getExceptionMessage());
//        }
//        if (!nicknameService.isAvailableNickname(member.getNickname())) {
//            throw new IllegalStateException(ExceptionMessage.UNAVAILABLE_NICKNAME.getExceptionMessage());
//        }
//        if (!isAvailableDomain) {
//            throw new NoSuchElementException(ExceptionMessage.UNIV_DOMAIN_NOT_FOUND.getExceptionMessage());
//        }
//        memberRepository.save(member);
//        applicationEventPublisher.publishEvent(new MemberSignUpEvent(member));
    }

    private boolean isAvailableEmail(String email) {
        return memberRepository.findByEmail(email).isPresent();
    }
}
