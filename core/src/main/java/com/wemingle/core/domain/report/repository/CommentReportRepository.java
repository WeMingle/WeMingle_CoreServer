package com.wemingle.core.domain.report.repository;

import com.wemingle.core.domain.member.entity.Member;
import com.wemingle.core.domain.report.entity.CommentReportEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Optional;

public interface CommentReportRepository extends JpaRepository<CommentReportEntity,Long> {
    @Query("select c from CommentReportEntity c " +
            "where c.reporter = :reporter and c.reportCommentId = :reportCommentId ")
    Optional<CommentReportEntity> findDuplicateCommentReport(@Param("reporter") Member reporter, @Param("reportCommentId") Long reportCommentId);

}
