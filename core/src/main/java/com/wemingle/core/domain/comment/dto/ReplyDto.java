package com.wemingle.core.domain.comment.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

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
}
