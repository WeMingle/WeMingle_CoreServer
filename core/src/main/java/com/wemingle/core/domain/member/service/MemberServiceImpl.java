package com.wemingle.core.domain.member.service;

import com.wemingle.core.domain.member.entity.Member;
import com.wemingle.core.domain.member.repository.MemberRepository;
import com.wemingle.core.domain.member.vo.SignupVo;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import static com.wemingle.core.global.exceptionmessage.ExceptionMessage.MEMBER_NOT_FOUNT;

@RequiredArgsConstructor
@Service
@Transactional(readOnly = true)
public class MemberServiceImpl implements MemberService {
    private final MemberRepository memberRepository;

    @Override
    public boolean verifyAvailableId(String memberId) {

    }

    @Transactional
    @Override
    public void saveMember(SignupVo.SaveMemberVo saveMemberVo) {

    //todo 어떤 계정으로 회원가입되었는지 확인 후 리턴할 것(예: 카카오로 이미 가입된 학교 이메일입니다)
    }

    @Override
    public void patchMemberProfile(SignupVo.PatchMemberProfileVo patchMemberProfileVo) {

    }

    @Override
    public Member findByRefreshToken(String refreshToken) {
        return memberRepository.findByRefreshToken(refreshToken)
                .orElseThrow(() -> new EntityNotFoundException(MEMBER_NOT_FOUNT.getExceptionMessage()));
    }

    @Override
    public Member findByMemberId(String memberId) {
        return memberRepository.findByMemberId(memberId)
                .orElseThrow(() -> new EntityNotFoundException(MEMBER_NOT_FOUNT.getExceptionMessage()));
    }
}
