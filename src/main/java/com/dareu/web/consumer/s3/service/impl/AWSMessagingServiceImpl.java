package com.dareu.web.consumer.s3.service.impl;

import com.amazonaws.services.sqs.AmazonSQS;
import com.amazonaws.services.sqs.model.SendMessageRequest;
import com.dareu.web.consumer.s3.exception.AWSMessageException;
import com.dareu.web.consumer.s3.service.AWSMessagingService;
import com.dareu.web.dto.jms.ErrorMessageRequest;
import com.dareu.web.dto.jms.PushNotificationRequest;
import com.google.gson.Gson;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Component
public class AWSMessagingServiceImpl implements AWSMessagingService {

    @Autowired
    @Qualifier("gson")
    private Gson gson;

    @Autowired
    @Qualifier("amazonSQS")
    private AmazonSQS amazonSQS;

    @Value("${com.dareu.web.jms.push.queue}")
    private String pushNotificationsDestinationName;

    @Value("$errors.queue.name}")
    private String errorsDestinationName;


    private final Logger log = Logger.getLogger(getClass());

    @Override
    public void sendPushNotificationMessage(PushNotificationRequest queueMessage) throws AWSMessageException {
        final String queueUrl = amazonSQS.getQueueUrl(pushNotificationsDestinationName).getQueueUrl();
        amazonSQS.sendMessage(new SendMessageRequest(queueUrl, gson.toJson(queueMessage)));
    }

    @Override
    public void sendErrorMessage(ErrorMessageRequest request) throws AWSMessageException {
        final String queueUrl = amazonSQS.getQueueUrl(errorsDestinationName).getQueueUrl();
        amazonSQS.sendMessage(new SendMessageRequest(queueUrl, gson.toJson(request)));
    }
}
