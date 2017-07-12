package com.dareu.web.consumer.s3.config;

import com.amazonaws.auth.AWSCredentials;
import com.amazonaws.auth.AWSCredentialsProvider;
import com.amazonaws.auth.BasicAWSCredentials;
import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.AmazonS3Client;
import com.google.gson.Gson;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.beans.factory.config.PropertyPlaceholderConfigurer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.springframework.context.support.PropertySourcesPlaceholderConfigurer;

import java.text.DateFormat;
import java.text.SimpleDateFormat;

@Configuration
@ComponentScan("com.dareu.web.consumer.push")
@Import(JmsConfiguration.class)
public class CommonBeansConfiguration {

    @Value("${s3.aws.access.key}")
    private String accessKey;

    @Value("${s3.aws.secret.key}")
    private String secretKey;

    @Bean(name = "gson")
    public Gson gson(){
        return new Gson();
    }

    @Bean(name = "amazonS3")
    public AmazonS3 amazonS3(){
        return new AmazonS3Client(s3CredentialsProvider());
    }

    @Bean(name = "dateFormat")
    public DateFormat dateFormat(){
        return new SimpleDateFormat("MM-dd-YYYY HH:ss");
    }

    @Bean
    public static PropertySourcesPlaceholderConfigurer pspc() {
        return new PropertySourcesPlaceholderConfigurer();
    }

    @Bean(name = "s3AwsCredentialsprovider")
    public AWSCredentialsProvider s3CredentialsProvider(){
        return new AWSCredentialsProvider() {
            public AWSCredentials getCredentials() {
                return new BasicAWSCredentials(accessKey, secretKey);
            }

            public void refresh() {

            }
        };
    }
}
