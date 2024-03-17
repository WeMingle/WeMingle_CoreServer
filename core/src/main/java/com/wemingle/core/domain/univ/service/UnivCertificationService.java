package com.wemingle.core.domain.univ.service;

import com.wemingle.core.domain.univ.entity.UnivEntity;
import com.wemingle.core.domain.univ.repository.UnivRepository;
import com.wemingle.core.global.exceptionmessage.ExceptionMessage;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.regex.Pattern;

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

        return pattern.matcher(mailAddress).group(1);
    }

    public UnivEntity findByDomain(String univDomain){
        return univRepository.findByDomain(univDomain).orElseThrow(() -> new EntityNotFoundException(
                ExceptionMessage.UNIV_DOMAIN_NOT_FOUND.getExceptionMessage()));
    }
}
