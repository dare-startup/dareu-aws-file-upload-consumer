package com.dareu.web.consumer.s3.service;

import com.dareu.web.consumer.s3.exception.AWSFileUploadException;
import com.dareu.web.dto.jms.PayloadMessage;

public interface AWSFileUploadService {
    public void uploadFile(PayloadMessage payloadMessage)throws AWSFileUploadException, Exception;
}
