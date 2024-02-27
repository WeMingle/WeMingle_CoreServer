package com.wemingle.core.domain.memberunivemail.service;

import com.wemingle.core.domain.member.entity.Member;
import com.wemingle.core.domain.memberunivemail.entity.VerifiedUniversityEmail;
import com.wemingle.core.domain.memberunivemail.repository.VerifiedUniversityEmailRepository;
import com.wemingle.core.domain.univ.entity.UnivEntity;
import lombok.RequiredArgsConstructor;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.util.concurrent.ConcurrentHashMap;

@Service
@RequiredArgsConstructor
public class MailVerificationService {
    private final VerifiedUniversityEmailRepository verifiedUniversityEmailRepository;
    private final int VERIFIED_CODE_LENGTH = 8;
    private static final int LIMIT_TIME = 300000;
    private final ConcurrentHashMap<String, VerificationCodeEntry> verificationCodes = new ConcurrentHashMap<>();
    private final JavaMailSender mailSender;

    public void sendVerificationMail(String univEmail, Member member) {

        String verificationCode = generateVerificationCode();

        saveVerificationCode(member.getMemberId(),verificationCode);

        SimpleMailMessage simpleMailMessage = new SimpleMailMessage();
        simpleMailMessage.setSubject("Wemingle 재학 인증");
        simpleMailMessage.setText(getMailVerificationText(member, verificationCode));
        simpleMailMessage.setTo(univEmail);
        mailSender.send(simpleMailMessage);

    }

    private String getMailVerificationText(Member member, String verificationCode) {

        return "안녕하세요 " + member.getNickname() + "님!" +
                "[Wemingle] 재학생 확인을 위해 인증번호 [" + verificationCode +"]를 입력해 주세요.";
    }

    public boolean validVerificationCode(String memberId, String verificationCode) {
        if (verificationCodes.contains(memberId)) {
            return verificationCodes.get(memberId).code().equals(verificationCode);
        }
        return false;
    }

    public void saveVerifiedUniversityEmail(Member member, UnivEntity univEntity) {
        verifiedUniversityEmailRepository.save(
                VerifiedUniversityEmail.builder()
                        .member(member)
                        .univName(univEntity)
                        .build()
        );
    }

    public void saveVerificationCode(String memberId, String verificationCode) {
        verificationCodes.put(memberId, new VerificationCodeEntry(verificationCode, System.currentTimeMillis()));
    }

    private record VerificationCodeEntry(String code, long creationTime) {

        public boolean isValid() {
            return System.currentTimeMillis() - creationTime <= LIMIT_TIME;
        }
    }

    public String generateVerificationCode() {
        try {
            SecureRandom random = SecureRandom.getInstanceStrong();
            StringBuilder codeBuilder = new StringBuilder();

            for (int i = 0; i < VERIFIED_CODE_LENGTH; i++) {
                codeBuilder.append(random.nextInt(10));
            }

            return codeBuilder.toString();
        } catch (NoSuchAlgorithmException e) {
            throw new RuntimeException(e);
        }
    }

    @Scheduled(fixedRate = 60000)
    public void expiredVerificationCodeCleaner() {
        verificationCodes.entrySet().removeIf(entry -> !entry.getValue().isValid());
    }
}
