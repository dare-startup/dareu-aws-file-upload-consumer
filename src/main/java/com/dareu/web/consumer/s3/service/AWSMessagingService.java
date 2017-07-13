package com.dareu.web.consumer.s3.service;

import com.dareu.web.consumer.s3.exception.AWSMessageException;
import com.messaging.dto.ErrorMessageRequest;
import com.messaging.dto.PushNotificationRequest;

public interface AWSMessagingService {
    public void sendPushNotificationMessage(PushNotificationRequest pushNotificationRequest)throws AWSMessageException;
    public void sendErrorMessage(ErrorMessageRequest request)throws AWSMessageException;
}
