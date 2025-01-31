package com.wemingle.core.domain.member.service;

import com.wemingle.core.domain.member.entity.redisprefix.RedisPrefix;
import com.wemingle.core.global.config.jwt.TokenProvider;
import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class BlacklistService {
    private final TokenProvider tokenProvider;
    private final RedisTemplate<String, String> redisTemplate;

    public boolean isBlacklistToken(String jwtToken) {
        return redisTemplate.hasKey(RedisPrefix.BLACKLIST_TOKEN.getPrefix() + jwtToken);
    }

    public void saveBlacklistToken(List<String> jwtToken) {
        jwtToken.iterator().forEachRemaining(token -> {
            redisTemplate.opsForValue().set(
                    RedisPrefix.BLACKLIST_TOKEN.getPrefix() + token,
                    "",
                    tokenProvider.getRemainingExpirationTime(token));
        });
    }
}
