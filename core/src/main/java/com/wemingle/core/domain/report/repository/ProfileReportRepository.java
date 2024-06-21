package com.wemingle.core.domain.report.repository;

import com.wemingle.core.domain.member.entity.Member;
import com.wemingle.core.domain.report.entity.ProfileReportEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDateTime;
import java.util.Optional;

public interface ProfileReportRepository extends JpaRepository<ProfileReportEntity,Long> {
    @Query("select c from CommentReportEntity c " +
            "where c.reporter = :reporter and c.reportedMember = :reportedMember and c.createdTime >= :sixMonthsAgo ")
    Optional<ProfileReportEntity> findDuplicateProfileReport(@Param("reporter") Member reporter, @Param("reportedMember") Member reportedMember, @Param("sixMonthsAgo") LocalDateTime sixMonthsAgo);
}
