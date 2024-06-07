package com.wemingle.core.domain.comment.controller;

import com.wemingle.core.domain.comment.dto.CommentDto;
import com.wemingle.core.domain.comment.service.CommentService;
import com.wemingle.core.global.responseform.ResponseHandler;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;

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
        commentService.updateComment(updateDto, userDetails.getUsername());

        return ResponseEntity.noContent().build();
    }

    @DeleteMapping
    public ResponseEntity<Object> deleteComment(@RequestBody CommentDto.RequestCommentDelete deleteDto,
                                                @AuthenticationPrincipal UserDetails userDetails){
        commentService.deleteComment(deleteDto, userDetails.getUsername());

        return ResponseEntity.noContent().build();
    }

    @GetMapping
    public ResponseEntity<ResponseHandler<HashMap<Long, CommentDto.ResponseCommentsInfoRetrieve>>> getComments(@RequestParam Long teamPostId,
                                                                                                               @RequestParam(required = false) Long nextIdx,
                                                                                                               @AuthenticationPrincipal UserDetails userDetails){
        HashMap<Long, CommentDto.ResponseCommentsInfoRetrieve> responseData = commentService.getComments(nextIdx, teamPostId, userDetails.getUsername());

        return ResponseEntity.ok(
                ResponseHandler.<HashMap<Long, CommentDto.ResponseCommentsInfoRetrieve>>builder()
                        .responseMessage("Comments retrieval successfully")
                        .responseData(responseData)
                        .build());
    }
}
