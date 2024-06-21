package com.wemingle.core.domain.report.dto;

import com.wemingle.core.domain.report.entity.reportitem.ReportItem;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.UUID;

public class RequestReportDto {

    @Getter
    @NoArgsConstructor
    public static class PostReportDto {
        private String reportedMember;
        private ReportItem reportItem;
        private Long reportPostId;
        private String reportPostContent;

        @Builder
        public PostReportDto(String reportedMember, ReportItem reportItem, Long reportPostId, String reportPostContent) {
            this.reportedMember = reportedMember;
            this.reportItem = reportItem;
            this.reportPostId = reportPostId;
            this.reportPostContent = reportPostContent;
        }
    }

    @Getter
    @NoArgsConstructor
    public static class ProfileReportDto {
        private String reportedMember;
        private ReportItem reportItem;
        private String nickname;
        private UUID profileImgId;

        @Builder
        public ProfileReportDto(String reportedMember, ReportItem reportItem, String nickname, UUID profileImgId) {
            this.reportedMember = reportedMember;
            this.reportItem = reportItem;
            this.nickname = nickname;
            this.profileImgId = profileImgId;
        }
    }

    @Getter
    @NoArgsConstructor
    public static class CommentReportDto{
        private String reportedMember;
        private ReportItem reportItem;
        private Long reportCommentId;
        private String reportCommentContent;
        @Builder
        public CommentReportDto(String reportedMember, ReportItem reportItem, Long reportCommentId, String reportCommentContent) {
            this.reportedMember = reportedMember;
            this.reportItem = reportItem;
            this.reportCommentId = reportCommentId;
            this.reportCommentContent = reportCommentContent;
        }
    }

    @Getter
    @NoArgsConstructor
    public static class ChatReportDto{
        private String reportedMember;
        private ReportItem reportItem;
        private UUID roomId;

        @Builder
        public ChatReportDto(String reportedMember, ReportItem reportItem, UUID roomId) {
            this.reportedMember = reportedMember;
            this.reportItem = reportItem;
            this.roomId = roomId;
        }
    }
}
