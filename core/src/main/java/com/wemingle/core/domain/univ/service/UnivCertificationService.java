package com.wemingle.core.domain.univ.service;

import com.wemingle.core.domain.univ.entity.UnivEntity;
import com.wemingle.core.domain.univ.repository.UnivRepository;
import com.wemingle.core.global.exceptionmessage.ExceptionMessage;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Slf4j
@RequiredArgsConstructor
@Service
public class UnivCertificationService {
    private final UnivRepository univRepository;

    public boolean validUnivDomain(String univDomain) {
        return univRepository.findByDomain(univDomain).isPresent();
    }

    public String getDomainInMailAddress(String mailAddress) {
        String domainRegex = "@([a-zA-Z0-9.-]+)\\.";

        Pattern pattern = Pattern.compile(domainRegex);

        Matcher matcher = pattern.matcher(mailAddress);
        if (matcher.find()) {
            return matcher.group(1);
        }
        return "not found";
    }

    public UnivEntity findByDomain(String univDomain){
        return univRepository.findByDomain(univDomain).orElseThrow(() -> new EntityNotFoundException(
                ExceptionMessage.UNIV_DOMAIN_NOT_FOUND.getExceptionMessage()));
    }
}
