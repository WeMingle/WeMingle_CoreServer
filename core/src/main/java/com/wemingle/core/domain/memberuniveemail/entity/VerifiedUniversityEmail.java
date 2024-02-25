package com.wemingle.core.domain.memberuniveemail.entity;

import com.wemingle.core.domain.univ.entity.UnivEntity;
import com.wemingle.core.domain.member.entity.Member;
import jakarta.persistence.*;

import java.time.LocalDateTime;

@Entity
public class VerifiedUniversityEmail {

    @Id
    @Column(name = "PK")
    private Long pk;

    @JoinColumn(name = "member")
    @OneToOne(fetch = FetchType.LAZY)
    private Member member;

    @JoinColumn(name = "univName")
    @ManyToOne(fetch = FetchType.LAZY)
    private UnivEntity univName;

    @Column(name = "VERIFIED_DATE")
    private LocalDateTime verifiedDate;


}
