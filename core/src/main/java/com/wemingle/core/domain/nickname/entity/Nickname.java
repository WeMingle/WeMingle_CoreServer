package com.wemingle.core.domain.nickname.entity;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.ColumnDefault;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Entity
@Table(name = "nickname")
public class Nickname {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", nullable = false)
    private Long id;

    @Column(name = "NICKNAME")
    private String nickname;

    @ColumnDefault("true")
    @Column(name = "AVAILABLE")
    private Boolean available;

    @Builder
    public Nickname(Boolean available) {
        this.available = available;
    }
}