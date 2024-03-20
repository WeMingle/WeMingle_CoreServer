package com.wemingle.core.global.exception;

public class InvalidRoleException extends RuntimeException{
    public InvalidRoleException() {
        super("유효하지 않은 Role입니다");
    }
}
