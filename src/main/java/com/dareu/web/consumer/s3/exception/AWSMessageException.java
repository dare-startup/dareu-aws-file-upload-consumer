package com.dareu.web.consumer.s3.exception;

public class AWSMessageException extends Exception {
    public AWSMessageException(String message) {
        super(message);
    }

    public AWSMessageException(String message, Throwable cause) {
        super(message, cause);
    }
}
