package com.wemingle.core.domain.comment.controller;

import com.wemingle.core.domain.comment.dto.ReplyDto;
import com.wemingle.core.domain.comment.service.ReplyService;
import com.wemingle.core.global.responseform.ResponseHandler;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/reply")
public class ReplyController {
    private final ReplyService replyService;

    @PostMapping
    public ResponseEntity<Object> saveReply(@RequestBody @Valid ReplyDto.RequestReplySave saveDto,
                                            @AuthenticationPrincipal UserDetails userDetails){
        replyService.saveReply(saveDto, userDetails.getUsername());

        return ResponseEntity.noContent().build();
    }

    @PatchMapping
    public ResponseEntity<Object> updateReply(@RequestBody @Valid ReplyDto.RequestReplyUpdate updateDto,
                                              @AuthenticationPrincipal UserDetails userDetails){
        if (!replyService.isReplyWriter(updateDto.getReplyPk(), userDetails.getUsername())){
            return ResponseEntity.status(HttpStatus.FORBIDDEN)
                    .body(ResponseHandler.builder()
                            .responseMessage("Only writer can update reply")
                            .build());
        }

        replyService.updateReply(updateDto);

        return ResponseEntity.noContent().build();
    }

    @DeleteMapping
    public ResponseEntity<Object> deleteReply(@RequestBody ReplyDto.RequestReplyDelete deleteDto,
                                              @AuthenticationPrincipal UserDetails userDetails){
        if (!replyService.isReplyWriter(deleteDto.getReplyPk(), userDetails.getUsername())){
            return ResponseEntity.status(HttpStatus.FORBIDDEN)
                    .body(ResponseHandler.builder()
                            .responseMessage("Only writer can delete reply")
                            .build());
        }

        replyService.deleteReply(deleteDto);

        return ResponseEntity.noContent().build();
    }

    @GetMapping
    public ResponseEntity<ResponseHandler<ReplyDto.ResponseRepliesRetrieve>> getReplies(@RequestParam Long nextIdx,
                                                                                        @RequestParam Long commentPk,
                                                                                        @AuthenticationPrincipal UserDetails userDetails){
        ReplyDto.ResponseRepliesRetrieve responseData = replyService.getReplies(nextIdx, commentPk, userDetails.getUsername());

        return ResponseEntity.ok(
                ResponseHandler.<ReplyDto.ResponseRepliesRetrieve>builder()
                        .responseMessage("Replies retrieval successfully")
                        .responseData(responseData)
                        .build()
                );
    }
}
