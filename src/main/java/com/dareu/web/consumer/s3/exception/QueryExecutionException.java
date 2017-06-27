package com.dareu.web.consumer.s3.exception;

public class QueryExecutionException extends Exception {
    public QueryExecutionException(String message) {
        super(message);
    }

    public QueryExecutionException(String message, Throwable cause) {
        super(message, cause);
    }
}
