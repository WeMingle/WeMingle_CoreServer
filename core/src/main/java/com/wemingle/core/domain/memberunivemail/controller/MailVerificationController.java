package com.wemingle.core.domain.memberunivemail.controller;

import com.wemingle.core.domain.memberunivemail.service.MailVerificationService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@RestController
@RequestMapping("/verify")
@RequiredArgsConstructor
public class MailVerificationController {
    private final MailVerificationService mailVerificationService;

//    @GetMapping("/{verificationId}")
//    public ResponseEntity<?> verifyEmail(@PathVariable("verificationId") String verificationId) {
//        log.info(verificationId);
//        log.info(new String(Base64.getDecoder().decode(verificationId),StandardCharsets.UTF_8));
//        UUID decodedVerificationId = UUID.fromString(new String(Base64.getDecoder().decode(verificationId), StandardCharsets.UTF_8));
//        boolean isverified = mailVerificationService.isPresentMemberIdByVerificationId(decodedVerificationId);
//        if (isverified) {
//            return ResponseEntity.ok().build();
//        }else return ResponseEntity.badRequest().build();
//    }

}
