package com.wemingle.core.domain.comment.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.NotBlank;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDate;
import java.util.HashMap;

public class ReplyDto {
    @Getter
    @Setter
    @NoArgsConstructor
    public static class RequestReplySave{
        private Long commentPk;
        @NotBlank(message = "대댓글은 최소 1글자 이상 작성해야합니다.")
        private String content;
    }

    @Getter
    @Setter
    @NoArgsConstructor
    public static class RequestReplyUpdate{
        private Long replyPk;
        @NotBlank(message = "대댓글은 최소 1글자 이상 작성해야합니다.")
        private String content;
    }

    @Getter
    @Setter
    @NoArgsConstructor
    public static class RequestReplyDelete{
        private Long replyPk;
    }

    @Getter
    @Setter
    @NoArgsConstructor
    public static class ResponseRepliesRetrieve{
        private String nextUrl;
        private HashMap<Long, ReplyInfo> repliesInfo;

        @Builder
        public ResponseRepliesRetrieve(String nextUrl, HashMap<Long, ReplyInfo> repliesInfo) {
            this.nextUrl = nextUrl;
            this.repliesInfo = repliesInfo;
        }
    }

    @Getter
    @Setter
    @NoArgsConstructor
    public static class ReplyInfo {
        private String imgUrl;
        private String nickname;
        private String content;
        private LocalDate createDate;
        @JsonProperty(value = "isWriter")
        private boolean isWriter;
        @JsonProperty(value = "isLocked")
        private boolean isLocked;
        @JsonProperty(value = "isDeleted")
        private boolean isDeleted;

        @Builder
        public ReplyInfo(String imgUrl, String nickname, String content, LocalDate createDate, boolean isWriter, boolean isLocked, boolean isDeleted) {
            this.imgUrl = imgUrl;
            this.nickname = nickname;
            this.content = content;
            this.createDate = createDate;
            this.isWriter = isWriter;
            this.isLocked = isLocked;
            this.isDeleted = isDeleted;
        }
    }
}
