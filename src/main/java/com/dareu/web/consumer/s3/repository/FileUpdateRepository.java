package com.dareu.web.consumer.s3.repository;

import com.dareu.web.consumer.s3.exception.QueryExecutionException;
import com.dareu.web.dto.jms.FileUploadProperties;

public interface FileUpdateRepository {
    public void updateEntityFileUploadUrl(String id, String awsUrl, FileUploadProperties.DareuFileType dareuFileType)throws QueryExecutionException;
    public String getFcmToken(String userId, EntityType entityType) throws QueryExecutionException, Exception;

    public static enum EntityType{
        USER, RESPONSE
    }
}
