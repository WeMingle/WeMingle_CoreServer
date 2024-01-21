package com.wemingle.core.domain.mail.controller;

import com.wemingle.core.domain.mail.service.MailVerificationService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.nio.charset.StandardCharsets;
import java.util.Arrays;
import java.util.Base64;
import java.util.UUID;

@Slf4j
@RestController
@RequestMapping("/verify")
@RequiredArgsConstructor
public class MailVerificationController {
    private final MailVerificationService mailVerificationService;

    @GetMapping("/{verificationId}")
    public ResponseEntity<?> verifyEmail(@PathVariable("verificationId") String verificationId) {
        log.info(verificationId);
        log.info(new String(Base64.getDecoder().decode(verificationId),StandardCharsets.UTF_8));
        UUID decodedVerificationId = UUID.fromString(new String(Base64.getDecoder().decode(verificationId), StandardCharsets.UTF_8));
        boolean isverified = mailVerificationService.isPresentMemberIdByVerificationId(decodedVerificationId);
        if (isverified) {
            return ResponseEntity.ok().build();
        }else return ResponseEntity.badRequest().build();
    }

}
