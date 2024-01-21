package com.wemingle.core.domain.mail.service;

import com.wemingle.core.domain.user.entity.Member;
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
