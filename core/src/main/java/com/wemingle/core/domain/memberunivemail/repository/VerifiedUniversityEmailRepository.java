package com.wemingle.core.domain.memberunivemail.repository;


import com.wemingle.core.domain.member.entity.signupplatform.SignupPlatform;
import com.wemingle.core.domain.memberunivemail.entity.VerifiedUniversityEmail;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Optional;

public interface VerifiedUniversityEmailRepository extends JpaRepository<VerifiedUniversityEmail, Long> {
    Optional<VerifiedUniversityEmail> findByUnivEmailAddress(String univEmailAddress);

    @Query("select v.member.signupPlatform " +
            "from VerifiedUniversityEmail v " +
            "where v.univEmailAddress = :univEmail")
    Optional<SignupPlatform> findPlatformUsedForRegistration(@Param("univEmail") String email);
}
