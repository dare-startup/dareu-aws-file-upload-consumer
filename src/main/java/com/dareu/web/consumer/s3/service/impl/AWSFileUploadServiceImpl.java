package com.dareu.web.consumer.s3.service.impl;

import com.amazonaws.AmazonServiceException;
import com.amazonaws.SdkClientException;
import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.model.CannedAccessControlList;
import com.amazonaws.services.s3.model.PutObjectRequest;
import com.dareu.web.consumer.s3.exception.AWSFileUploadException;
import com.dareu.web.consumer.s3.repository.FileUpdateRepository;
import com.dareu.web.consumer.s3.service.AWSFileUploadService;
import com.dareu.web.consumer.s3.service.AWSMessagingService;
import com.google.gson.Gson;
import com.messaging.dto.FileUploadRequest;
import com.messaging.dto.PushNotificationPayload;
import com.messaging.dto.PushNotificationRequest;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.io.File;

@Component
public class AWSFileUploadServiceImpl implements AWSFileUploadService {

    @Value("${com.dareu.web.s3.profile.bucket.name}")
    private String profileBucket;

    @Value("${com.dareu.web.s3.thumbs.bucket.name}")
    private String thumbsBucket;

    @Value("${com.dareu.web.s3.uploads.bucket.name}")
    private String videoBucket;

    @Value("${com.dareu.web.s3.url.format}")
    private String s3UrlFormat;

    @Value("${dareu.multipart.tmp.directory}")
    private String tmpDirectory;

    @Autowired
    @Qualifier("amazonS3")
    private AmazonS3 amazonS3;

    @Autowired
    @Qualifier("gson")
    private Gson gson;

    @Autowired
    private AWSMessagingService awsMessagingService;

    @Autowired
    private FileUpdateRepository fileUpdateRepository;

    private final Logger log = Logger.getLogger(getClass());

    public void uploadFile(FileUploadRequest properties) throws AWSFileUploadException {
        //get message
        File  file = new File(tmpDirectory + properties.getFileName());
        if(file.exists()){
            String[] array = properties.getFileName().split("\\.");
            if(array.length == 0)
                throw new AWSFileUploadException(String.format("Looks like we have a little bug here, we cannot split the file name using '.'"));
            final String entityId = array[0];
            final FileUploadRequest.DareuFileType fileType = FileUploadRequest.DareuFileType.fromString(properties.getFileType());
            if(fileType == null)
                throw new AWSFileUploadException(String.format("FileType %s is not valid"));
            log.info(String.format("Uploading file to S3 service: Starting upload of %s", file.getAbsolutePath()));
            String currentBucket;
            String fcmToken;
            //choose bucket name and get fcm token
            try{
                switch(fileType){
                    case PROFILE:
                        currentBucket = profileBucket;
                        fcmToken = fileUpdateRepository.getFcmToken(entityId, FileUpdateRepository.EntityType.USER);
                        break;
                    case RESPONSE:
                        currentBucket = videoBucket;
                        fcmToken = fileUpdateRepository.getFcmToken(entityId, FileUpdateRepository.EntityType.RESPONSE);
                        break;
                    case RESPONSE_THUMB:
                        currentBucket = thumbsBucket;
                        fcmToken = fileUpdateRepository.getFcmToken(entityId, FileUpdateRepository.EntityType.RESPONSE);
                        break;
                    default:
                        final String errorMessage = String.format("FileType %s not supported", properties.getFileType().toString());
                        log.info(errorMessage);
                        throw new AWSFileUploadException(errorMessage);
                }
                //upload file
                final PutObjectRequest putObjectRequest = new PutObjectRequest(currentBucket, file.getName(), file);
                putObjectRequest.setCannedAcl(CannedAccessControlList.PublicRead);
                amazonS3.putObject(putObjectRequest);

                //generate url
                final String url = String.format(s3UrlFormat, currentBucket, file.getName());

                // send message to push notifications service to notify user
                if(fcmToken != null && ! fcmToken.isEmpty()){
                    PushNotificationRequest pushNotificationMessage =
                            new PushNotificationRequest(fcmToken, new PushNotificationPayload("dareu.upload.complete", url));
                    awsMessagingService.sendPushNotificationMessage(pushNotificationMessage);
                }


                //delete tmp file
                file.delete();
            } catch(ArrayIndexOutOfBoundsException ex){
                log.error(String.format("ArrayIndexOutOfBoundsException: %s", ex.getMessage()));
                throw new AWSFileUploadException("Could not upload file to S3 service, check Amazon Service and server log details");
            } catch(AmazonServiceException ex){
                log.error(String.format("AmazonServiceException: %s", ex.getMessage()));
                throw new AWSFileUploadException("Could not upload file to S3 service, check Amazon Service and server log details");
            } catch(SdkClientException ex){
                log.error(String.format("SdkClientException: %s", ex.getMessage()));
                throw new AWSFileUploadException("Could not upload file to S3 service, check Amazon SDK configuration and server log details");
            } catch(Exception ex){
                log.error(ex.getMessage());
                throw new AWSFileUploadException("An unknown error occurred, check server log for details");
            }
        }else{
            log.error(String.format("Cannot upload file to S3 service: File %s does not exists on %s", file.getAbsolutePath(), tmpDirectory));
        }
    }
}
