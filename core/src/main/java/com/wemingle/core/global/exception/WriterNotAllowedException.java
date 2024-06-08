package com.wemingle.core.global.exception;

public class WriterNotAllowedException extends RuntimeException{
    public WriterNotAllowedException() {
        super("This action is not allowed for the writer.");
    }
}
