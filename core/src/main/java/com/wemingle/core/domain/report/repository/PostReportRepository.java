package com.wemingle.core.domain.report.repository;

import com.wemingle.core.domain.member.entity.Member;
import com.wemingle.core.domain.report.entity.PostReportEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Optional;

public interface PostReportRepository extends JpaRepository<PostReportEntity, Long> {
    @Query("select p from PostReportEntity p " +
            "where p.reporter = :reporter and p.reportPostId = :reportPostId ")
    Optional<PostReportEntity> findDuplicateCommentReport(@Param("reporter") Member reporter, @Param("reportPostId") Long reportPostId);

    @Query("select count(*) from PostReportEntity p " +
            "where p.reportedMember = :reportedMember and p.reportPostId = :reportPostId")
    Integer findReportCntByReportedMemberAndReportPostId(@Param("reportedMember") Member reportedMember, @Param("reportPostId") Long reportPostId);

    @Query("select count(*) from PostReportEntity p " +
            "where p.reportPostId = :reportPostId")
    Integer findReportCntByPostId(@Param("reportPostId") Long reportPostId);
}
