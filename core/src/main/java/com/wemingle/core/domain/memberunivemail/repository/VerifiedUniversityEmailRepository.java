package com.wemingle.core.domain.memberunivemail.repository;


import com.wemingle.core.domain.member.entity.Member;
import com.wemingle.core.domain.member.entity.signupplatform.SignupPlatform;
import com.wemingle.core.domain.memberunivemail.entity.VerifiedUniversityEmail;
import com.wemingle.core.domain.univ.entity.UnivEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface VerifiedUniversityEmailRepository extends JpaRepository<VerifiedUniversityEmail, Long> {
    Optional<VerifiedUniversityEmail> findByUnivEmailAddress(String univEmailAddress);

    @Query("select v.member.signupPlatform " +
            "from VerifiedUniversityEmail v " +
            "where v.univEmailAddress = :univEmail")
    Optional<SignupPlatform> findPlatformUsedForRegistration(@Param("univEmail") String email);

    Optional<VerifiedUniversityEmail> findByMember(Member member);
    @Query("select v.univName from VerifiedUniversityEmail v where v.member = :member")
    Optional<UnivEntity> findUnivEntityByMember(@Param("member")Member member);
    @Query("select v.member from VerifiedUniversityEmail v where v.univName = :univ and v.member != :member")
    List<Member> findUnivMates(@Param("univ") UnivEntity univ, @Param("member")Member member);
}
