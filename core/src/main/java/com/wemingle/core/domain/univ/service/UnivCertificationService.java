package com.wemingle.core.domain.univ.service;

import com.wemingle.core.domain.univ.repository.UnivRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.regex.Matcher;
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
}
