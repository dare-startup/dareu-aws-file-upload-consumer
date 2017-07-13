package com.dareu.web.consumer.s3.service;

import com.dareu.web.consumer.s3.exception.AWSFileUploadException;
import com.messaging.dto.upload.FileUploadRequest;

public interface AWSFileUploadService {
    public void uploadFile(FileUploadRequest payloadMessage)throws AWSFileUploadException, Exception;
}
