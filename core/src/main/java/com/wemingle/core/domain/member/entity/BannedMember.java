package com.wemingle.core.domain.member.entity;

import com.wemingle.core.domain.member.entity.bantype.BanType;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;

import java.time.LocalDateTime;

@Entity
public class BannedMember {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "pk")
    private Long pk;

    @NotNull
    @Column(name = "BAN_START_TIME")
    private LocalDateTime banStartTime;

    @NotNull
    @Column(name = "BAN_END_TIME")
    private LocalDateTime banEndTime;

    @NotNull
    @Enumerated(EnumType.STRING)
    private BanType banType;
}
