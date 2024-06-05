package com.wemingle.core.domain.nickname.service;

import com.wemingle.core.domain.matching.repository.TeamRequestRepository;
import com.wemingle.core.domain.member.repository.MemberRepository;
import com.wemingle.core.domain.nickname.service.nicknamecleaner.NicknameCleaner;
import com.wemingle.core.domain.team.repository.TeamMemberRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Slf4j
@RequiredArgsConstructor
@Service
public class NicknameService {
    private final MemberRepository memberRepository;
    private final TeamMemberRepository teamMemberRepository;
    private final TeamRequestRepository teamRequestRepository;
    public boolean isAvailableNickname(String nickname) {
        NicknameCleaner nicknameCleaner = new NicknameCleaner();
        return nicknameCleaner.isCleanNickname(nickname) && memberRepository.findByNickname(nickname).isEmpty();
    }

    public boolean isAvailableNicknameInTeam(Long teamPk, String nickname) {
        NicknameCleaner nicknameCleaner = new NicknameCleaner();
        return nicknameCleaner.isCleanNickname(nickname)
                && teamMemberRepository.findByTeam_PkAndNickname(teamPk, nickname).isEmpty()
                && teamRequestRepository.findByTeam_PkAndNickname(teamPk, nickname).isEmpty();
    }
}
