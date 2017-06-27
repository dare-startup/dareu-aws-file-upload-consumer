package com.dareu.web.consumer.s3.service.impl;

import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.model.CannedAccessControlList;
import com.amazonaws.services.s3.model.PutObjectRequest;
import com.dareu.web.consumer.s3.exception.AWSFileUploadException;
import com.dareu.web.consumer.s3.repository.FileUpdateRepository;
import com.dareu.web.consumer.s3.service.AWSFileUploadService;
import com.dareu.web.consumer.s3.service.AWSMessagingService;
import com.dareu.web.dto.jms.FileUploadProperties;
import com.dareu.web.dto.jms.PayloadMessage;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;

import java.io.File;

@Component
public class AWSFileUploadServiceImpl implements AWSFileUploadService {

    private static final String PROFILE_BUCKET = "dareu-profiles";
    private static final String THUMB_BUCKET = "dareu-thumbs";
    private static final String VIDEO_BUCKET = "dareu-uploads";
    private static final String BASE_URL = "https://%s.s3.amazonaws.com/%s";

    private final Logger log = Logger.getLogger(getClass());

    @Autowired
    @Qualifier("amazonS3")
    private AmazonS3 amazonS3;

    @Autowired
    private AWSMessagingService awsMessagingService;

    @Autowired
    private FileUpdateRepository fileUpdateRepository;

    public void uploadFile(PayloadMessage payloadMessage) throws AWSFileUploadException {
        //get message
        FileUploadProperties properties = (FileUploadProperties)payloadMessage.getData();
        File  file = new File(properties.getCurrentFilePath());
        final String entityId = file.getName().split(".")[0];
        if(file.exists()){
            log.info(String.format("Uploading file to S3 service: Starting upload of %s", file.getAbsolutePath()));
            String currentBucket;
            try{
                switch(properties.getFileType()){
                    case PROFILE:
                        currentBucket = PROFILE_BUCKET;
                        break;
                    case RESPONSE:
                        currentBucket = VIDEO_BUCKET;
                        break;
                    case RESPONSE_THUMB:
                        currentBucket = THUMB_BUCKET;
                        break;
                    default:
                        final String errorMessage = String.format("FileType %s not supported", properties.getFileType().toString());
                        log.info(errorMessage);
                        throw new AWSFileUploadException(errorMessage);
                }
                final PutObjectRequest putObjectRequest = new PutObjectRequest(currentBucket, file.getName(), file);
                putObjectRequest.setCannedAcl(CannedAccessControlList.PublicRead);
                amazonS3.putObject(putObjectRequest);
                //generate url
                final String url = String.format(BASE_URL, currentBucket, file.getName());
            }catch(Exception ex){
                log.error(String.format("Could not upload file %s to AWS S3 service", file.getAbsolutePath()));
                throw new AWSFileUploadException(ex.getMessage());
            }
        }else{
            log.info(String.format("Cannot upload file to S3 service: File %s does not exists", file.getAbsolutePath()));
        }
    }
}
