package com.dareu.web.consumer.s3.listener;

import com.amazon.sqs.javamessaging.message.SQSTextMessage;
import com.dareu.web.consumer.s3.exception.AWSFileUploadException;
import com.dareu.web.consumer.s3.exception.AWSMessageException;
import com.dareu.web.consumer.s3.service.AWSFileUploadService;
import com.dareu.web.consumer.s3.service.AWSMessagingService;
import com.dareu.web.dto.jms.ErrorMessageRequest;
import com.dareu.web.dto.jms.PayloadMessage;
import com.google.gson.Gson;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.messaging.Message;
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

    public void onMessage(Message message){
        if(message instanceof SQSTextMessage){
            SQSTextMessage sqsTextMessage = (SQSTextMessage)message;
            try{
                //get payload
                final String payload = sqsTextMessage.getText();
                PayloadMessage payloadMessage = gson.fromJson(payload, PayloadMessage.class);
                //upload file
                awsFileUploadService.uploadFile(payloadMessage);
            } catch(JMSException ex){
                try{
                    awsMessagingService.sendErrorMessage(new ErrorMessageRequest(ex.getMessage(),
                            dateFormat.format(new Date()), getClass().getPackage().getName()));
                } catch(AWSMessageException messageException){
                    logger.fatal(messageException);
                }
                logger.error(ex.getMessage());
            } catch(AWSFileUploadException ex){
                logger.error(ex.getMessage());
            } catch(Exception ex){
                logger.error(ex.getMessage());
            }
        }else{
            logger.info("Message is not SQSTextMessage: " + message.toString());
        }
    }
}
