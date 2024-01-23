package com.wemingle.core.domain.nickname.repository;

import com.wemingle.core.domain.nickname.entity.Nickname;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface NicknameRepository extends JpaRepository<Nickname, Long> {
    @Query("select n.nickname " +
            "from Nickname n " +
            "where n.available = true " +
            "order by function('rand') ")
    List<String> findAvailableNicknames(Pageable pageable);

    @Query("select n.available " +
            "from Nickname n " +
            "where n.nickname = :nickname")
    boolean isAvailableNickname(@Param("nickname") String nickname);
}