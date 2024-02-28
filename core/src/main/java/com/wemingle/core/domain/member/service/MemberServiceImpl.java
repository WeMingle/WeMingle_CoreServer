package com.wemingle.core.domain.member.service;

import com.wemingle.core.domain.member.entity.Member;
import com.wemingle.core.domain.member.repository.MemberRepository;
import com.wemingle.core.domain.member.vo.SignupVo;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import static com.wemingle.core.global.exceptionmessage.ExceptionMessage.MEMBER_NOT_FOUNT;

@RequiredArgsConstructor
@Service
@Transactional(readOnly = true)
public class MemberServiceImpl implements MemberService {
    private final MemberRepository memberRepository;
    private final BCryptPasswordEncoder bCryptPasswordEncoder;

    @Override
    public boolean verifyAvailableId(String memberId) {
        return memberRepository.findByMemberId(memberId).isEmpty();
    }

    @Override
    @Transactional
    public void saveMember(SignupVo.SaveMemberVo saveMemberVo) {
        saveMemberVo.patchPassword(bCryptPasswordEncoder.encode(saveMemberVo.getPassword()));

        Member member = saveMemberVo.of(saveMemberVo);
        memberRepository.save(member);
    }

    @Override
    @Transactional
    public void patchMemberProfile(SignupVo.PatchMemberProfileVo patchMemberProfileVo) {
        Member findMember = findByMemberId(patchMemberProfileVo.getMemberId());
        String nickname = patchMemberProfileVo.getNickname();;
        String profileImgName = uploadMemberProfileImg(patchMemberProfileVo.getMemberProfileImg());

        findMember.patchMemberProfile(nickname, profileImgName);
    }

    private String uploadMemberProfileImg(MultipartFile memberProfileImg) {
        //todo 이미지를 s3/local에 올리는 로직 향후에 구현
        return "Dummy";
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
