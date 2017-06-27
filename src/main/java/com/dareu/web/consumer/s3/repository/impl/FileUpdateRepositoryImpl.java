package com.dareu.web.consumer.s3.repository.impl;

import com.dareu.web.consumer.s3.exception.QueryExecutionException;
import com.dareu.web.consumer.s3.repository.FileUpdateRepository;
import com.dareu.web.dto.jms.FileUploadProperties;
import org.apache.log4j.Logger;
import org.springframework.stereotype.Component;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

@Component
public class FileUpdateRepositoryImpl implements FileUpdateRepository {

    @PersistenceContext
    private EntityManager entityManager;

    private final Logger logger = Logger.getLogger(getClass());

    public void updateEntityFileUploadUrl(String id, String awsUrl, FileUploadProperties.DareuFileType dareuFileType) throws QueryExecutionException {
        String queryValue;
        switch(dareuFileType){
            case PROFILE:
                queryValue = "UPDATE dareu_user SET image_url = ?1 WHERE id = ?2";
                break;
            case RESPONSE:
                queryValue = "UPDATE dare_response SET video_url = ?1 WHERE id = ?2";
                break;
            case RESPONSE_THUMB:
                queryValue = "UPDATE dare_response SET thumb_url = ?1 WHERE id = ?2";
                break;
            default:
                throw new IllegalArgumentException("No file type was provided");
        }
        try{
            logger.info(String.format("Will update %s entity"));
            entityManager.createNativeQuery(queryValue)
                    .setParameter(1, awsUrl)
                    .setParameter(2, id)
                    .executeUpdate();

        }catch(Exception ex){
            throw new QueryExecutionException(ex.getMessage());
        }
    }
}
