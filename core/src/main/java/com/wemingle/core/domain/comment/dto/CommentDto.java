package com.wemingle.core.domain.comment.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

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
}
