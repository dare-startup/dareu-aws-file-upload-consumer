package com.dareu.web.consumer.s3.service;

import com.dareu.web.consumer.s3.exception.AWSMessageException;
import com.dareu.web.dto.jms.ErrorMessageRequest;
import com.dareu.web.dto.jms.QueueMessage;

public interface AWSMessagingService {
    public void sendPushNotificationMessage(QueueMessage queueMessage)throws AWSMessageException;
    public void sendErrorMessage(ErrorMessageRequest request)throws AWSMessageException;
}
