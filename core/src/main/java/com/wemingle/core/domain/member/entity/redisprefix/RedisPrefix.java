package com.wemingle.core.domain.member.entity.redisprefix;

import lombok.Getter;

@Getter
public enum RedisPrefix {
    BLACKLIST_TOKEN("blacklist:token:");

    private final String prefix;

    RedisPrefix(String prefix) {
        this.prefix = prefix;
    }
}
