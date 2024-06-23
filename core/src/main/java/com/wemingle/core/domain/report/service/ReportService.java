package com.wemingle.core.domain.report.service;

import com.wemingle.core.domain.member.entity.Member;
import com.wemingle.core.domain.member.service.MemberService;
import com.wemingle.core.domain.report.dto.RequestReportDto;
import com.wemingle.core.domain.report.entity.ChatReportEntity;
import com.wemingle.core.domain.report.entity.CommentReportEntity;
import com.wemingle.core.domain.report.entity.PostReportEntity;
import com.wemingle.core.domain.report.entity.ProfileReportEntity;
import com.wemingle.core.domain.report.entity.reporttype.ReportPath;
import com.wemingle.core.domain.report.repository.ChatReportRepository;
import com.wemingle.core.domain.report.repository.CommentReportRepository;
import com.wemingle.core.domain.report.repository.PostReportRepository;
import com.wemingle.core.domain.report.repository.ProfileReportRepository;
import com.wemingle.core.global.exceptionmessage.ExceptionMessage;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.NoSuchElementException;

@Service
@RequiredArgsConstructor
@Slf4j
public class ReportService {
    private final PostReportRepository postReportRepository;
    private final ProfileReportRepository profileReportRepository;
    private final CommentReportRepository commentReportRepository;
    private final ChatReportRepository chatReportRepository;
    private final MemberService memberService;

    private final int REPORT_POST_CNT_LIMIT = 5;
    private final int REPORT_COMMENT_CNT_LIMIT = 5;

    @Transactional
    public void savePostReport(String reporterId, RequestReportDto.PostReportDto postReportDto) {
        Member reporter = memberService.findByMemberId(reporterId);
        Member reportedMember = memberService.findByMemberId(postReportDto.getReportedMember());

        if (isDuplicatePostReport(postReportDto.getReportPostId(), reporter)) {
            throw new RuntimeException("이미 신고한 사용자");
        }

        PostReportEntity postReportEntity = PostReportEntity.builder()
                .reportItem(postReportDto.getReportItem())
                .reportPath(ReportPath.PROFILE)
                .reporter(reporter)
                .reportedMember(reportedMember)
                .reportPostId(postReportDto.getReportPostId())
                .reportPostContent(postReportDto.getReportPostContent())
                .build();

        postReportRepository.save(postReportEntity);

        incrementPostWarningCount(postReportDto.getReportPostId(), reportedMember);
    }

    public void incrementPostWarningCount(Long reportPostId, Member reportedMember) {
        if (isReportPostCntLimitExceeded(reportPostId)) {
            incrementMemberWarningCount(reportedMember);
        }
    }

    private boolean isReportPostCntLimitExceeded(Long reportPostId) {
        Integer reportCnt = postReportRepository.findReportCntByPostId(reportPostId);
        return reportCnt == REPORT_POST_CNT_LIMIT;
    }

    private void incrementMemberWarningCount(Member memberId) {
        int complaintsCount = memberId.getComplaintsCount();
        memberId.setComplaintsCount(++complaintsCount);
    }

    private boolean isDuplicatePostReport(Long reportPostId, Member reporter) {
        return postReportRepository.findDuplicateCommentReport(reporter, reportPostId).isPresent();
    }

    public void saveProfileReport(String reporterId, RequestReportDto.ProfileReportDto profileReportDto) {
        Member reporter = memberService.findByMemberId(reporterId);
        Member reportedMember = memberService.findByMemberId(profileReportDto.getReportedMember());

        if (isExistDuplicateProfileReport(reporter, reportedMember)) {
            throw new RuntimeException("이미 신고한 사용자");
        }

        ProfileReportEntity profileReportEntity = ProfileReportEntity.builder()
                .reportItem(profileReportDto.getReportItem())
                .reportPath(ReportPath.PROFILE)
                .reporter(reporter)
                .reportedMember(reportedMember)
                .nickname(profileReportDto.getNickname())
                .profileImgId(profileReportDto.getProfileImgId())
                .build();

        profileReportRepository.save(profileReportEntity);
    }

    private boolean isExistDuplicateProfileReport(Member reporter, Member reportedMember) {
        LocalDateTime sixMonthAge = LocalDateTime.now().minusMonths(6);
        return profileReportRepository.findDuplicateProfileReport(reporter, reportedMember, sixMonthAge).isPresent();
    }

    public void saveCommentReport(String reporterId, RequestReportDto.CommentReportDto commentReportDto) {
        Member reporter = memberService.findByMemberId(reporterId);
        Member reportedMember = memberService.findByMemberId(commentReportDto.getReportedMember());

        if (isExistDuplicateCommentReport(reporter, commentReportDto.getReportCommentId())) {
            throw new RuntimeException("이미 신고한 사용자");
        }

        CommentReportEntity commentReportEntity = CommentReportEntity.builder()
                .reportItem(commentReportDto.getReportItem())
                .reportPath(ReportPath.COMMENT)
                .reporter(reporter)
                .reportedMember(reportedMember)
                .reportCommentId(commentReportDto.getReportCommentId())
                .reportCommentContent(commentReportDto.getReportCommentContent())
                .build();
        commentReportRepository.save(commentReportEntity);

        incrementCommentWarningCount(commentReportDto.getReportCommentId(),reportedMember);
    }

    public void incrementCommentWarningCount(Long reportCommentId, Member reportedMember) {
        if (isReportCommentCntLimitExceeded(reportCommentId)) {
            incrementMemberWarningCount(reportedMember);
        }
    }

    private boolean isReportCommentCntLimitExceeded(Long reportCommentId) {
        Integer reportCnt = commentReportRepository.findReportCntByCommentId(reportCommentId);
        return reportCnt == REPORT_COMMENT_CNT_LIMIT;
    }

    private boolean isExistDuplicateCommentReport(Member reporter, Long reportCommentId) {
        return commentReportRepository.findDuplicateCommentReport(reporter, reportCommentId).isPresent();
    }

    public void saveChatReport(String reporterId, RequestReportDto.ChatReportDto chatReportDto) {
        Member reporter = memberService.findByMemberId(reporterId);
        Member reportedMember = memberService.findByMemberId(chatReportDto.getReportedMember());

        if (isExistDuplicateChatReport(reporter, reportedMember)) {
            throw new RuntimeException("이미 신고한 사용자");
        }

        ChatReportEntity chatReportEntity = ChatReportEntity.builder()
                .reportItem(chatReportDto.getReportItem())
                .reportPath(ReportPath.CHAT)
                .reporter(reporter)
                .reportedMember(reportedMember)
                .reportRoomId(chatReportDto.getRoomId())
                .build();
        chatReportRepository.save(chatReportEntity);
    }

    private boolean isExistDuplicateChatReport(Member reporter, Member reportedMember) {
        LocalDateTime sixMonthAge = LocalDateTime.now().minusMonths(6);
        return chatReportRepository.findDuplicateChatReport(reporter, reportedMember, sixMonthAge).isPresent();
    }
}
