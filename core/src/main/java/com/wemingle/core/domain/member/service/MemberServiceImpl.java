package com.wemingle.core.domain.member.service;

import com.wemingle.core.domain.category.sports.entity.SportsCategory;
import com.wemingle.core.domain.category.sports.entity.sportstype.SportsType;
import com.wemingle.core.domain.category.sports.repository.SportsCategoryRepository;
import com.wemingle.core.domain.member.entity.Member;
import com.wemingle.core.domain.member.entity.MemberPreferenceSports;
import com.wemingle.core.domain.member.entity.PolicyTerms;
import com.wemingle.core.domain.member.entity.signupplatform.SignupPlatform;
import com.wemingle.core.domain.member.repository.MemberPreferenceSportsRepository;
import com.wemingle.core.domain.member.repository.MemberRepository;
import com.wemingle.core.domain.member.repository.PolicyTermsRepository;
import com.wemingle.core.domain.member.vo.SignupVo;
import com.wemingle.core.global.exception.CannotSaveImgException;
import com.wemingle.core.global.util.fileextension.FileExtensionFilter;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.util.List;
import java.util.UUID;

import static com.wemingle.core.global.exceptionmessage.ExceptionMessage.CANNOT_SAVE_IMG;
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

    @Value("${member-profile.store.path}")
    private String memberProfilePath;

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
        String nickname = patchMemberProfileVo.getNickname();;
        String profileImgName = uploadMemberProfileImg(patchMemberProfileVo.getMemberProfileImg());

        findMember.patchMemberProfile(nickname, profileImgName);
    }

    private String uploadMemberProfileImg(MultipartFile memberProfileImg) {
        //todo 이미지를 s3에 올리는 로직 향후에 구현
        FileExtensionFilter fileExtensionFilter = new FileExtensionFilter();
        String fileExtension = getFileExtension(memberProfileImg);

        fileExtensionFilter.isAvailableFileExtension(fileExtension);
        String fileName = UUID.randomUUID() + "." + fileExtension;

        saveImgInStorage(memberProfileImg, fileName);
        return fileName;
    }

    private static String getFileExtension(MultipartFile memberProfileImg) {
        String fileOriginalName = memberProfileImg.getOriginalFilename();
        return fileOriginalName.substring(fileOriginalName.lastIndexOf(".") + 1);
    }

    private void saveImgInStorage(MultipartFile memberProfileImg, String fileName) {
        try {
            memberProfileImg.transferTo(new File(memberProfilePath + fileName));
        }catch (IOException e){
            throw new CannotSaveImgException(CANNOT_SAVE_IMG.getExceptionMessage());
        }
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
}