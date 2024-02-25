package com.wemingle.core.domain.memberunivemail.service;

import com.wemingle.core.domain.member.entity.Member;
import lombok.Getter;
import org.springframework.context.ApplicationEvent;

@Getter
public class MemberSignUpEvent extends ApplicationEvent {

    private Member member;

    public MemberSignUpEvent(Member member) {
        super(member);
        this.member = member;
    }


}
