package com.dareu.web.consumer.s3.exception;

public class AWSFileUploadException extends Exception {
    public AWSFileUploadException(String message) {
        super(message);
    }

    public AWSFileUploadException(String message, Throwable cause) {
        super(message, cause);
    }
}
