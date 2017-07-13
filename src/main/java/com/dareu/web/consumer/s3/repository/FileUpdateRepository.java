package com.dareu.web.consumer.s3.repository;

import com.dareu.web.consumer.s3.exception.QueryExecutionException;
import com.messaging.dto.FileUploadRequest;

public interface FileUpdateRepository {
    public void updateEntityFileUploadUrl(String id, String awsUrl, FileUploadRequest.DareuFileType dareuFileType)throws QueryExecutionException;
    public String getFcmToken(String userId, EntityType entityType) throws QueryExecutionException, Exception;

    public static enum EntityType{
        USER, RESPONSE
    }
}
