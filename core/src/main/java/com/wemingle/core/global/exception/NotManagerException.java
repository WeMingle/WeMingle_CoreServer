package com.wemingle.core.global.exception;

public class NotManagerException extends RuntimeException{
    public NotManagerException() {
        super("Not the manager");
    }
}
