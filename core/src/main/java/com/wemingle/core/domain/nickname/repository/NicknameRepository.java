package com.wemingle.core.domain.nickname.repository;

import com.wemingle.core.domain.nickname.entity.Nickname;
import org.springframework.data.jpa.repository.JpaRepository;

public interface NicknameRepository extends JpaRepository<Nickname, Long> {
}