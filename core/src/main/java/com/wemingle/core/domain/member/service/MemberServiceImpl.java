package com.wemingle.core.domain.member.service;

import com.wemingle.core.domain.category.sports.entity.SportsCategory;
import com.wemingle.core.domain.category.sports.entity.sportstype.SportsType;
import com.wemingle.core.domain.category.sports.repository.SportsCategoryRepository;
import com.wemingle.core.domain.member.dto.MemberAuthenticationInfoDto;
import com.wemingle.core.domain.member.dto.MemberInfoDto;
import com.wemingle.core.domain.member.entity.Member;
import com.wemingle.core.domain.member.entity.MemberPreferenceSports;
import com.wemingle.core.domain.member.entity.PolicyTerms;
import com.wemingle.core.domain.member.entity.signupplatform.SignupPlatform;
import com.wemingle.core.domain.member.repository.MemberPreferenceSportsRepository;
import com.wemingle.core.domain.member.repository.MemberRepository;
import com.wemingle.core.domain.member.repository.PolicyTermsRepository;
import com.wemingle.core.domain.member.vo.SignupVo;
import com.wemingle.core.domain.memberunivemail.entity.VerifiedUniversityEmail;
import com.wemingle.core.domain.memberunivemail.repository.VerifiedUniversityEmailRepository;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.NoSuchElementException;

import static com.wemingle.core.global.exceptionmessage.ExceptionMessage.MEMBER_NOT_FOUNT;

@RequiredArgsConstructor
@Service
@Transactional(readOnly = true)
public class MemberServiceImpl implements MemberService {
    private final MemberRepository memberRepository;
    private final BCryptPasswordEncoder bCryptPasswordEncoder;
    private final PolicyTermsRepository policyTermsRepository;
    private final SportsCategoryRepository sportsCategoryRepository;
    private final MemberPreferenceSportsRepository memberPreferenceSportsRepository;
    private final VerifiedUniversityEmailRepository verifiedUniversityEmailRepository;

    @Override
    public boolean verifyAvailableId(String memberId) {
        return memberRepository.findByMemberId(memberId).isEmpty();
    }

    @Override
    @Transactional
    public void saveMember(SignupVo.SaveMemberVo saveMemberVo) {
        PolicyTerms policyTerms = savePolicyTerms(saveMemberVo.isAgreeToLocationBasedServices(), saveMemberVo.isAgreeToReceiveMarketingInformation());
        saveMemberVo.patchPassword(bCryptPasswordEncoder.encode(saveMemberVo.getPassword()));
        Member member = saveMemberVo.of(saveMemberVo);
        member.patchPolicyTerms(policyTerms);

        memberRepository.save(member);
    }

    private PolicyTerms savePolicyTerms(boolean agreeToLocationBasedServices, boolean agreeToReceiveMarketingInformation){
        return policyTermsRepository.save(PolicyTerms.builder()
                .agreeToLocationBasedServices(agreeToLocationBasedServices)
                .agreeToReceiveMarketingInformation(agreeToReceiveMarketingInformation)
                .build());
    }

    @Override
    @Transactional
    public void patchMemberProfile(SignupVo.PatchMemberProfileVo patchMemberProfileVo) {
        Member findMember = findByMemberId(patchMemberProfileVo.getMemberId());
        String nickname = patchMemberProfileVo.getNickname();

        findMember.patchMemberProfile(nickname);
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

    @Override
    public boolean isRegisteredMember(String memberId, SignupPlatform platform) {
        return memberRepository.findByMemberId(memberId)
                .map(member -> member.getSignupPlatform().toString().equals(platform.toString()))
                .orElse(false);
    }

    @Override
    @Transactional
    public void saveMemberPreferenceSports(String memberId, List<SportsType> preferenceSports) {
        Member findMember = findByMemberId(memberId);
        List<SportsCategory> preferenceSportsCategories = sportsCategoryRepository.findBySportsTypes(preferenceSports);

        List<MemberPreferenceSports> memberPreferenceSportsList = preferenceSportsCategories.stream()
                .map(preferenceSportsCategory -> MemberPreferenceSports.builder()
                        .member(findMember)
                        .sports(preferenceSportsCategory)
                        .build())
                .toList();

        memberPreferenceSportsRepository.saveAll(memberPreferenceSportsList);
    }

    @Override
    public MemberInfoDto getMemberInfo(String memberId) {
        Member member = memberRepository.findByMemberId(memberId).orElseThrow(() -> new NoSuchElementException(MEMBER_NOT_FOUNT.getExceptionMessage()));
        return MemberInfoDto.builder().oneLineIntroduction(member.getOneLineIntroduction())
                .nickname(member.getNickname())
                .isAbilityPublic(member.isAbilityPublic())
                .ability(member.getAbility())
                .gender(member.getGender())
                .numberOfMatches(member.getNumberOfMatches())
                .isMajorActivityAreaPublic(member.isMajorActivityAreaPublic())
                .majorActivityArea(member.getMajorActivityArea())
                .oneLineIntroduction(member.getOneLineIntroduction())
                .build();
    }

    @Override
    @Transactional
    public void setMemberInfo(String memberId, MemberInfoDto memberInfoDto) {
        Member member = memberRepository.findByMemberId(memberId).orElseThrow(() -> new NoSuchElementException(MEMBER_NOT_FOUNT.getExceptionMessage()));

        member.setNickname(memberInfoDto.getNickname());
        member.setMajorActivityAreaPublic(memberInfoDto.isMajorActivityAreaPublic());
        member.setMajorActivityArea(memberInfoDto.getMajorActivityArea());
        member.setNumberOfMatches(memberInfoDto.getNumberOfMatches());
        member.setAbilityPublic(memberInfoDto.isAbilityPublic());
        member.setGender(memberInfoDto.getGender());
        member.setOneLineIntroduction(memberInfoDto.getOneLineIntroduction());
        member.setAbility(memberInfoDto.getAbility());
    }

    @Override
    public MemberAuthenticationInfoDto getMemberAuthenticationInfo(String memberId) {
        Member member = memberRepository.findByMemberId(memberId).orElseThrow(() -> new NoSuchElementException(MEMBER_NOT_FOUNT.getExceptionMessage()));
        VerifiedUniversityEmail verifiedUniversityEmail = verifiedUniversityEmailRepository.findByMember(member)
                .orElse(
                        VerifiedUniversityEmail.builder()
                        .univEmailAddress("University authentication has not been completed")
                        .build()
                );
        return MemberAuthenticationInfoDto.builder()
                .memberId(member.getMemberId())
                .univEmail(verifiedUniversityEmail.getUnivEmailAddress())
                .build();
    }
}