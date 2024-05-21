package com.wemingle.core.domain.comment.controller;

import com.wemingle.core.domain.comment.dto.CommentDto;
import com.wemingle.core.domain.comment.service.CommentService;
import com.wemingle.core.global.responseform.ResponseHandler;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/comment")
public class CommentController {
    private final CommentService commentService;

    @PostMapping
    public ResponseEntity<Object> saveComment(@RequestBody @Valid CommentDto.RequestCommentSave saveDto,
                                              @AuthenticationPrincipal UserDetails userDetails){
        commentService.saveComment(saveDto, userDetails.getUsername());

        return ResponseEntity.noContent().build();
    }

    @PatchMapping
    public ResponseEntity<Object> updateComment(@RequestBody @Valid CommentDto.RequestCommentUpdate updateDto,
                                              @AuthenticationPrincipal UserDetails userDetails){
        if (!commentService.isCommentWriter(updateDto.getCommentPk(), userDetails.getUsername())){
            return ResponseEntity.status(HttpStatus.FORBIDDEN)
                    .body(ResponseHandler.builder()
                            .responseMessage("Only writer can update comment")
                            .build());
        }

        commentService.updateComment(updateDto);

        return ResponseEntity.noContent().build();
    }

    @DeleteMapping
    public ResponseEntity<Object> deleteComment(@RequestBody CommentDto.RequestCommentDelete deleteDto,
                                                @AuthenticationPrincipal UserDetails userDetails){
        if (!commentService.isCommentWriter(deleteDto.getCommentPk(), userDetails.getUsername())){
            return ResponseEntity.status(HttpStatus.FORBIDDEN)
                    .body(ResponseHandler.builder()
                            .responseMessage("Only writer can delete comment")
                            .build());
        }

        commentService.deleteComment(deleteDto);

        return ResponseEntity.noContent().build();
    }
}