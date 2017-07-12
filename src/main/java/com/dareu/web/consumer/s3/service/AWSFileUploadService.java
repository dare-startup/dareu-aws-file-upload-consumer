package com.dareu.web.consumer.s3.service;

import com.dareu.web.consumer.s3.exception.AWSFileUploadException;
import com.dareu.web.dto.jms.FileUploadRequest;

public interface AWSFileUploadService {
    public void uploadFile(FileUploadRequest payloadMessage)throws AWSFileUploadException, Exception;
}
