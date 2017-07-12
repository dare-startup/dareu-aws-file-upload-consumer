package com.dareu.web.consumer.s3.service;

import com.dareu.web.consumer.s3.exception.AWSMessageException;
import com.dareu.web.dto.jms.ErrorMessageRequest;
import com.dareu.web.dto.jms.PushNotificationRequest;

public interface AWSMessagingService {
    public void sendPushNotificationMessage(PushNotificationRequest pushNotificationRequest)throws AWSMessageException;
    public void sendErrorMessage(ErrorMessageRequest request)throws AWSMessageException;
}
