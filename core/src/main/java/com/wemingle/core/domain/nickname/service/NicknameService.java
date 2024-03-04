package com.wemingle.core.domain.nickname.service;

import com.wemingle.core.domain.nickname.repository.NicknameRepository;
import com.wemingle.core.domain.nickname.service.nicknamecleaner.NicknameCleaner;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Slf4j
@RequiredArgsConstructor
@Service
public class NicknameService {
    private final NicknameRepository nicknameRepository;
    public boolean isAvailableNickname(String nickname) {
        NicknameCleaner nicknameCleaner = new NicknameCleaner();
        return nicknameRepository.isExistsNickname(nickname)!=null
                && !nicknameRepository.isExistsNickname(nickname)
                && nicknameCleaner.isCleanNickname(nickname);

    }
}
