package com.wemingle.core.domain.report.controller;

import com.wemingle.core.domain.report.dto.RequestReportDto;
import com.wemingle.core.domain.report.entity.CommentReportEntity;
import com.wemingle.core.domain.report.service.ReportService;
import com.wemingle.core.global.responseform.ResponseHandler;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/reports")
@Slf4j
public class ReportController {
    private final ReportService reportService;

    @PostMapping("/posts")
    ResponseEntity<ResponseHandler<Object>> createPostReport(@RequestBody RequestReportDto.PostReportDto postReportDto,
                                                             @AuthenticationPrincipal UserDetails userDetails) {
        reportService.savePostReport(userDetails.getUsername(), postReportDto);
        return ResponseEntity.noContent().build();
    }

    @PostMapping("/comments")
    ResponseEntity<ResponseHandler<Object>> createChatReport(@RequestBody RequestReportDto.CommentReportDto commentReportDto,
                                                             @AuthenticationPrincipal UserDetails userDetails) {
        reportService.saveCommentReport(userDetails.getUsername(), commentReportDto);
        return ResponseEntity.noContent().build();
    }

    @PostMapping("/chats")
    ResponseEntity<ResponseHandler<Object>> createChatReport(@RequestBody RequestReportDto.ChatReportDto chatReportDto,
                                                             @AuthenticationPrincipal UserDetails userDetails) {
        reportService.saveChatReport(userDetails.getUsername(), chatReportDto);
        return ResponseEntity.noContent().build();
    }

    @PostMapping("/profiles")
    ResponseEntity<ResponseHandler<Object>> createProfileReport(@RequestBody RequestReportDto.ProfileReportDto profileReportDto,
                                                                @AuthenticationPrincipal UserDetails userDetails) {
        reportService.saveProfileReport(userDetails.getUsername(), profileReportDto);
        return ResponseEntity.noContent().build();
    }
}
