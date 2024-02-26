package com.wemingle.core.domain.history.entity;

import com.wemingle.core.domain.common.entity.BaseEntity;
import com.wemingle.core.domain.history.entity.notificationType.NotificationType;
import com.wemingle.core.domain.history.entity.readstatus.ReadStatus;
import com.wemingle.core.domain.member.entity.Member;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;

@Entity
public class NotificationHistory extends BaseEntity {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "pk")
    private Long pk;

    @NotNull
    @Column(name = "MESSAGE")
    private String message;

    @Column (name = "OBJECT_ID")
    private Long objectId;

    @NotNull
    @Enumerated(EnumType.STRING)
    private NotificationType notificationType;

    @NotNull
    @Enumerated(EnumType.STRING)
    private ReadStatus readStatus;

    @NotNull
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "MEMBER")
    private Member member;
}
