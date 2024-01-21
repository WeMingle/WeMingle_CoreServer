package com.wemingle.core.domain.user.service;

import com.wemingle.core.domain.mail.service.MemberSignUpEvent;
import com.wemingle.core.domain.user.entity.Member;
import com.wemingle.core.domain.user.repository.MemberRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Service;

@RequiredArgsConstructor
@Service
public class MemberService {
    private final MemberRepository memberRepository;
    private final ApplicationEventPublisher applicationEventPublisher;
    public void saveMember(Member member) {
        memberRepository.save(member);
        applicationEventPublisher.publishEvent(new MemberSignUpEvent(member));
    }
}
