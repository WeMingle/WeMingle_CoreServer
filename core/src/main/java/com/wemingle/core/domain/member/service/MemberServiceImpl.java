package com.wemingle.core.domain.member.service;

import com.wemingle.core.domain.category.sports.entity.sportstype.SportsType;
import com.wemingle.core.domain.img.service.S3ImgService;
import com.wemingle.core.domain.member.dto.MemberAuthenticationInfoDto;
import com.wemingle.core.domain.member.dto.MemberDto;
import com.wemingle.core.domain.member.dto.MemberInfoDto;
import com.wemingle.core.domain.member.entity.Member;
import com.wemingle.core.domain.member.entity.MemberAbility;
import com.wemingle.core.domain.member.entity.PolicyTerms;
import com.wemingle.core.domain.member.entity.role.Role;
import com.wemingle.core.domain.member.entity.signupplatform.SignupPlatform;
import com.wemingle.core.domain.member.repository.MemberAbilityRepository;
import com.wemingle.core.domain.member.repository.MemberPreferenceSportsRepository;
import com.wemingle.core.domain.member.repository.MemberRepository;
import com.wemingle.core.domain.member.repository.PolicyTermsRepository;
import com.wemingle.core.domain.member.vo.SignupVo;
import com.wemingle.core.domain.memberunivemail.entity.VerifiedUniversityEmail;
import com.wemingle.core.domain.memberunivemail.repository.VerifiedUniversityEmailRepository;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Optional;

import static com.wemingle.core.global.exceptionmessage.ExceptionMessage.MEMBER_NOT_FOUNT;

@Slf4j
@RequiredArgsConstructor
@Service
@Transactional(readOnly = true)
public class MemberServiceImpl implements MemberService {
    private final MemberRepository memberRepository;
    private final BCryptPasswordEncoder bCryptPasswordEncoder;
    private final PolicyTermsRepository policyTermsRepository;
    private final MemberPreferenceSportsRepository memberPreferenceSportsRepository;
    private final VerifiedUniversityEmailRepository verifiedUniversityEmailRepository;
    private final S3ImgService s3ImgService;
    private final MemberAbilityRepository memberAbilityRepository;
    private final BCryptPasswordEncoder pwEncoder;

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
    public boolean isRegisteredMember(String memberId) {
        Optional<Member> byMemberId = memberRepository.findByMemberId(memberId);
        return byMemberId.isPresent();
    }

    @Override
    public SignupPlatform findRegisteredPlatformByMember(String memberId) {
        Member member = findByMemberId(memberId);
        return member.getSignupPlatform();
    }

    @Override
    public boolean isMatchesPassword(String memberId, String rawPw) {
        Member member = findByMemberId(memberId);
        return pwEncoder.matches(rawPw, member.getPassword());
    }

    @Override
    @Transactional
    public void saveMemberPreferenceSports(String memberId, SportsType preferenceSport) {
        Member findMember = findByMemberId(memberId);
        findMember.setPreferenceSport(preferenceSport);
    }

    @Override
    public MemberInfoDto getMemberInfo(String memberId) {
        Member member = findByMemberId(memberId);
        List<MemberInfoDto.EachAbilityAboutMember> abilityAboutMembers = memberAbilityRepository.findMemberAbilitiesByMember(member).stream().map(memberAbility -> MemberInfoDto.EachAbilityAboutMember.builder().ability(memberAbility.getAbility()).sportsType(memberAbility.getSportsType()).build()).toList();
        return MemberInfoDto.builder().oneLineIntroduction(member.getOneLineIntroduction())
                .nickname(member.getNickname())
                .isAbilityPublic(member.isAbilityPublic())
                .abilityList(abilityAboutMembers)
                .gender(member.getGender())
                .numberOfMatches(member.getNumberOfMatches())
                .isMajorActivityAreaPublic(member.isMajorActivityAreaPublic())
                .majorActivityArea(member.getMajorActivityArea())
                .oneLineIntroduction(member.getOneLineIntroduction())
                .profilePicId(member.getProfileImgId())
                .birthYear(member.getBirthYear())
                .build();
    }

    @Override
    @Transactional
    public void setMemberInfo(String memberId, MemberInfoDto memberInfoDto) {
        Member member = findByMemberId(memberId);

        member.setNickname(memberInfoDto.getNickname());
        member.setMajorActivityAreaPublic(memberInfoDto.isMajorActivityAreaPublic());
        member.setMajorActivityArea(memberInfoDto.getMajorActivityArea());
        member.setAbilityPublic(memberInfoDto.isAbilityPublic());
        member.setGender(memberInfoDto.getGender());
        member.setOneLineIntroduction(memberInfoDto.getOneLineIntroduction());
        member.setBirthYearPublic(memberInfoDto.isBirthYearPublic());
        member.setBirthYear(memberInfoDto.getBirthYear());
        memberRepository.save(member);

        clearMemberAbility(member);

        if (!memberInfoDto.getAbilityList().isEmpty()) {
            List<MemberAbility> memberAbilityList = memberInfoDto.getAbilityList().stream().map(
                    eachAbilityAboutMember -> MemberAbility.builder()
                            .sportsType(eachAbilityAboutMember.getSportsType())
                            .ability(eachAbilityAboutMember.getAbility())
                            .member(member)
                            .build()
            ).toList();
            memberAbilityRepository.saveAll(memberAbilityList);
        }
    }

    private void clearMemberAbility(Member member) {
        List<MemberAbility> memberAbilitiesByMember = memberAbilityRepository.findMemberAbilitiesByMember(member);
        memberAbilityRepository.deleteAll(memberAbilitiesByMember);
    }

    @Override
    public MemberAuthenticationInfoDto getMemberAuthenticationInfo(String memberId) {
        Member member = findByMemberId(memberId);
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

    @Override
    public HashMap<Long, MemberDto.ResponseMemberInfoInSearch> getMemberByNickname(Long nextIdx, String nickname, String memberId) {
        Member findMember = findByMemberId(memberId);
        List<Member> members = memberRepository.getMemberByNickname(nextIdx, nickname);

        LinkedHashMap<Long, MemberDto.ResponseMemberInfoInSearch> membersInfoHashMap = new LinkedHashMap<>();
        members.forEach(member -> membersInfoHashMap.put(member.getPk(), MemberDto.ResponseMemberInfoInSearch.builder()
                .nickname(member.getNickname())
                .profileImg(s3ImgService.getMemberProfilePicUrl(member.getProfileImgId()))
                .isMe(member.equals(findMember))
                .build()
        ));

        return membersInfoHashMap;
    }

    @Transactional
    @Override
    public void patchMemberPassword(String memberId, String newPassword) {
        Member member = findByMemberId(memberId);
        member.setPassword(newPassword);
    }

    @Override
    public Member findWithdrawMember() {
        return memberRepository.findByRole(Role.WITHDRAW_USER)
                .orElseThrow(() -> new EntityNotFoundException(MEMBER_NOT_FOUNT.getExceptionMessage()));
    }
}