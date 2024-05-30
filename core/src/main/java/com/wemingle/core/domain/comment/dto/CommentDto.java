package com.wemingle.core.domain.comment.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.NotBlank;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDate;

public class CommentDto {
    @Getter
    @Setter
    @NoArgsConstructor
    public static class RequestCommentSave{
        private Long teamPostPk;
        @NotBlank(message = "댓글은 최소 1글자 이상 작성해야합니다.")
        private String content;
    }

    @Getter
    @Setter
    @NoArgsConstructor
    public static class RequestCommentUpdate{
        private Long commentPk;
        @NotBlank(message = "댓글은 최소 1글자 이상 작성해야합니다.")
        private String content;
    }

    @Getter
    @Setter
    @NoArgsConstructor
    public static class RequestCommentDelete{
        private Long commentPk;
    }

    @Getter
    @Setter
    @NoArgsConstructor
    public static class ResponseCommentsInfoRetrieve {
        private String imgUrlWithComment;
        private String nicknameWithComment;
        private String contentWithComment;
        private LocalDate createDateWithComment;
        @JsonProperty(value = "isWriterWithComment")
        private boolean isWriterWithComment;
        @JsonProperty(value = "isLockedWithComment")
        private boolean isLockedWithComment;
        @JsonProperty(value = "isDeletedWithComment")
        private boolean isDeletedWithComment;
        private ReplyDto.ResponseRepliesRetrieve replies;

        @Builder
        public ResponseCommentsInfoRetrieve(String imgUrlWithComment, String nicknameWithComment, String contentWithComment, LocalDate createDateWithComment, boolean isWriterWithComment, boolean isLockedWithComment, boolean isDeletedWithComment, ReplyDto.ResponseRepliesRetrieve replies) {
            this.imgUrlWithComment = imgUrlWithComment;
            this.nicknameWithComment = nicknameWithComment;
            this.contentWithComment = contentWithComment;
            this.createDateWithComment = createDateWithComment;
            this.isWriterWithComment = isWriterWithComment;
            this.isLockedWithComment = isLockedWithComment;
            this.isDeletedWithComment = isDeletedWithComment;
            this.replies = replies;
        }
    }
}
