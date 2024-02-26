package com.wemingle.core.domain.member.service;

import com.wemingle.core.domain.member.dto.SetMemberProfileDto;
import com.wemingle.core.domain.nickname.service.NicknameService;
import com.wemingle.core.domain.univ.service.UnivCertificationService;
import com.wemingle.core.domain.member.entity.Member;
import com.wemingle.core.domain.member.repository.MemberRepository;
import com.wemingle.core.global.exceptionmessage.ExceptionMessage;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;


import static com.wemingle.core.global.exceptionmessage.ExceptionMessage.*;

@RequiredArgsConstructor
@Service
@Transactional(readOnly = true)
public class MemberService {
    private final MemberRepository memberRepository;
    private final ApplicationEventPublisher applicationEventPublisher;
    private final UnivCertificationService univCertificationService;
    private final NicknameService nicknameService;

    public Member findMemberByMemberId(String memberId) {
        return memberRepository.findByMemberId(memberId)
                .orElseThrow(() -> new RuntimeException(ExceptionMessage.MEMBER_NOT_FOUNT.toString()));
    }

    @Transactional
    public void setMemberProfile(Member member, SetMemberProfileDto setMemberProfileDto) {
        member.setMemberProfile("todo img id", setMemberProfileDto.getNickname()); //todo s3 img upload
    }

    @Transactional
    public void saveMember(Member member) {
    //todo 어떤 계정으로 회원가입되었는지 확인 후 리턴할 것(예: 카카오로 이미 가입된 학교 이메일입니다)

    }

    public Member findByRefreshToken(String refreshToken){
        return memberRepository.findByRefreshToken(refreshToken)
                .orElseThrow(() -> new EntityNotFoundException(MEMBER_NOT_FOUNT.getExceptionMessage()));

    }

    private boolean isAvailableEmail(String memberId) {
        return memberRepository.findByMemberId(memberId).isPresent();
    }
}
