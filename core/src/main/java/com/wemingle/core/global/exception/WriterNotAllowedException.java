package com.wemingle.core.global.exception;

public class NotWriterException extends RuntimeException{
    public NotWriterException() {
        super("Not the writer");
    }
}
