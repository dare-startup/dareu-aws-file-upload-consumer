package com.dareu.web.consumer.s3.config;

import com.amazonaws.auth.AWSCredentialsProvider;
import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.AmazonS3Client;
import com.google.gson.Gson;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.config.PropertyPlaceholderConfigurer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;

@Configuration
@ComponentScan("com.dareu.web.consumer.push")
@Import(JmsConfiguration.class)
public class CommonBeansConfiguration {

    @Autowired
    @Qualifier("awsCredentialsProvider")
    private AWSCredentialsProvider awsCredentialsProvider;

    @Bean
    public PropertyPlaceholderConfigurer propertyPlaceholderConfigurer(){
        PropertyPlaceholderConfigurer configurer = new PropertyPlaceholderConfigurer();
        configurer.setSystemPropertiesMode(PropertyPlaceholderConfigurer.SYSTEM_PROPERTIES_MODE_OVERRIDE);
        return configurer;
    }

    @Bean(name = "gson")
    public Gson gson(){
        return new Gson();
    }

    @Bean(name = "amazonS3")
    public AmazonS3 amazonS3(){
        return new AmazonS3Client(awsCredentialsProvider);
    }
}
