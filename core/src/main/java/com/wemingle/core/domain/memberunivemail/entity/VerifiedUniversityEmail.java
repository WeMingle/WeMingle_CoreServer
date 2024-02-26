package com.wemingle.core.domain.memberunivemail.entity;

import com.wemingle.core.domain.common.entity.BaseEntity;
import com.wemingle.core.domain.member.entity.Member;
import com.wemingle.core.domain.univ.entity.UnivEntity;
import jakarta.persistence.*;

@Entity
public class VerifiedUniversityEmail extends BaseEntity {
    @Id @Column(name = "PK")
    private Long pk;

    @JoinColumn(name = "MEMBER")
    @OneToOne(fetch = FetchType.LAZY)
    private Member member;

    @JoinColumn(name = "UNIV_NAME")
    @ManyToOne(fetch = FetchType.LAZY)
    private UnivEntity univName;
}
