package com.dareu.web.consumer.s3.listener;

import com.amazon.sqs.javamessaging.message.SQSTextMessage;
import com.dareu.web.consumer.s3.exception.AWSFileUploadException;
import com.dareu.web.consumer.s3.exception.AWSMessageException;
import com.dareu.web.consumer.s3.service.AWSFileUploadService;
import com.dareu.web.consumer.s3.service.AWSMessagingService;
import com.google.gson.Gson;
import com.messaging.dto.error.ErrorMessageRequest;
import com.messaging.dto.upload.FileUploadRequest;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;

import javax.jms.JMSException;
import java.text.DateFormat;
import java.util.Date;

@Component
public class FileUploadMessageListener {

    @Autowired
    @Qualifier("gson")
    private Gson gson;

    @Autowired
    private AWSFileUploadService awsFileUploadService;

    @Autowired
    private AWSMessagingService awsMessagingService;

    @Autowired
    @Qualifier("dateFormat")
    private DateFormat dateFormat;

    private final Logger logger = Logger.getLogger(getClass());

    public void onMessage(SQSTextMessage message) {
        try {
            //get payload
            final String payload = message.getText();
            FileUploadRequest payloadMessage = gson.fromJson(payload, FileUploadRequest.class);
            //upload file
            awsFileUploadService.uploadFile(payloadMessage);
        } catch (JMSException ex) {
            try {
                awsMessagingService.sendErrorMessage(new ErrorMessageRequest(ex.getMessage(),
                        dateFormat.format(new Date()), getClass().getPackage().getName()));
            } catch (AWSMessageException messageException) {
                logger.fatal(messageException);
            }
            logger.error(ex.getMessage());
        } catch (AWSFileUploadException ex) {
            logger.error(ex.getMessage());
        } catch (Exception ex) {
            logger.error(ex.getMessage());
        }

    }
}
