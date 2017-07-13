package com.dareu.web.consumer.s3.repository.impl;

import com.dareu.web.consumer.s3.exception.QueryExecutionException;
import com.dareu.web.consumer.s3.repository.FileUpdateRepository;
import com.messaging.dto.FileUploadRequest;
import org.apache.log4j.Logger;
import org.springframework.stereotype.Component;

import javax.persistence.EntityManager;
import javax.persistence.NoResultException;
import javax.persistence.PersistenceContext;

@Component
public class FileUpdateRepositoryImpl implements FileUpdateRepository {

    @PersistenceContext
    private EntityManager entityManager;

    private final Logger logger = Logger.getLogger(getClass());

    public void updateEntityFileUploadUrl(String id, String awsUrl, FileUploadRequest.DareuFileType dareuFileType) throws QueryExecutionException {
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

    @Override
    public String getFcmToken(String entityId, FileUpdateRepository.EntityType entityType) throws QueryExecutionException, Exception {
        try{
            switch (entityType){
                case RESPONSE:
                    return (String)entityManager.createNativeQuery("SELECT u.gcm_reg_id from dareu_user u inner join dare_response d on d.user_id = u.id where u.id = ?1")
                            .setParameter(1, entityId)
                            .getSingleResult();
                case USER:
                    return (String)entityManager.createNativeQuery("SELECT u.gcm_reg_id from dareu_user WHERE u.id = ?1")
                            .setParameter(1, entityId)
                            .getSingleResult();
                default:
                    throw new IllegalArgumentException("Entity type not supported");
            }
        }catch(NoResultException ex){
            return null;
        } catch(Exception ex){
            return null;
        }

    }
}
