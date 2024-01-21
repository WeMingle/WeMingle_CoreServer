package com.wemingle.core.domain.mail.service;

import com.wemingle.core.domain.user.entity.Member;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.ApplicationListener;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;
import org.springframework.transaction.event.TransactionPhase;
import org.springframework.transaction.event.TransactionalEventListener;

import java.nio.charset.StandardCharsets;
import java.util.Base64;
import java.util.UUID;

@Slf4j
@RequiredArgsConstructor
@Service
public class MemberSignUpListener  {//implements ApplicationListener<MemberSignUpEvent>

    private final JavaMailSender mailSender;
    private final MailVerificationService mailVerificationService;
    @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT,fallbackExecution = true)
    public void onApplicationEvent(MemberSignUpEvent event) {
        log.info("eventListener");
        String memberEmail = event.getMember().getEmail();
        UUID memberId = event.getMember().getMemberId();
        SimpleMailMessage simpleMailMessage = new SimpleMailMessage();
        simpleMailMessage.setSubject("Wemingle 대학 웸메일 인증");
        simpleMailMessage.setText(getVerificationText(event.getMember(), memberId));
        simpleMailMessage.setTo(memberEmail);
        mailSender.send(simpleMailMessage);

    }

    private String getVerificationText(Member member, UUID verificationId) {
        String encodedVerificationId = Base64.getEncoder()
                .encodeToString(verificationId.toString().getBytes(StandardCharsets.UTF_8));

        StringBuilder messageBuilder = new StringBuilder();
        messageBuilder.append("안녕하세요 ").append(member.getMemberName()).append("님!");
        messageBuilder.append("인증을 위해 다음 링크를 클릭하여 학교 인증을 완료해주세요:)").append("\n\n");
        messageBuilder.append("http://localhost:8080/verify/").append(encodedVerificationId);
        return messageBuilder.toString();

    }
}
