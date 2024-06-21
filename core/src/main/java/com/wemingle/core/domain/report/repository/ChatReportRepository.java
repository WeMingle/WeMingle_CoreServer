package com.wemingle.core.domain.report.repository;

import com.wemingle.core.domain.member.entity.Member;
import com.wemingle.core.domain.report.entity.ChatReportEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDateTime;
import java.util.Optional;

public interface ChatReportRepository extends JpaRepository<ChatReportEntity,Long> {

    @Query("select c from ChatReportEntity c " +
            "where c.reporter = :reporter and c.reportedMember = :reportedMember and c.createdTime >= :sixMonthsAgo ")
    Optional<ChatReportEntity> findDuplicateChatReport(@Param("reporter") Member reporter, @Param("reportedMember") Member reportedMember, @Param("sixMonthsAgo")LocalDateTime sixMonthsAgo);
}
