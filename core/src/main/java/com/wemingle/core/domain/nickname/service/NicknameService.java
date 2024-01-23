package com.wemingle.core.domain.nickname.service;

import com.wemingle.core.domain.nickname.repository.NicknameRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.List;

@RequiredArgsConstructor
@Service
public class NicknameService {
    private final NicknameRepository nicknameRepository;

    public List<String> generateNicknames(int count) {
        Pageable pageable = Pageable.ofSize(count);
        return nicknameRepository.findAvailableNicknames(pageable);
    }

    public boolean isAvailableNickname(String nickname) {
        return nicknameRepository.isAvailableNickname(nickname);
    }
}
