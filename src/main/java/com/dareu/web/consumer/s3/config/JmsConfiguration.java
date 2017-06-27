package com.dareu.web.consumer.s3.config;

import com.amazon.sqs.javamessaging.SQSConnectionFactory;
import com.amazonaws.auth.AWSCredentials;
import com.amazonaws.auth.AWSCredentialsProvider;
import com.amazonaws.auth.BasicAWSCredentials;
import com.amazonaws.regions.Region;
import com.amazonaws.regions.Regions;
import com.amazonaws.services.sqs.AmazonSQS;
import com.amazonaws.services.sqs.AmazonSQSClient;
import com.dareu.web.consumer.s3.listener.FileUploadMessageListener;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.jms.annotation.EnableJms;
import org.springframework.jms.listener.DefaultMessageListenerContainer;
import org.springframework.jms.listener.adapter.MessageListenerAdapter;

@Configuration
@EnableJms
@ComponentScan("com.dareu.web.consumer.s3")
public class JmsConfiguration {

    @Value("${sqs.aws.access.key}")
    private String awsAccessKey;

    @Value("${sqs.aws.secret.key}")
    private String awsSecretKey;

    @Value("${com.dareu.web.jms.push.queue}")
    private String destinationName;

    @Autowired
    private FileUploadMessageListener fileUploadMessageListener;

    @Bean(name = "sqsConnectionFactory")
    public SQSConnectionFactory sqsConnectionFactory() {
        SQSConnectionFactory factory = SQSConnectionFactory.builder()
                .withRegion(Region.getRegion(Regions.US_WEST_2))
                .withAWSCredentialsProvider(awsCredentialsProvider())
                .build();
        return factory;
    }

    @Bean(name = "awsCredentialsProvider")
    public AWSCredentialsProvider awsCredentialsProvider(){
        return new AWSCredentialsProvider() {
            public AWSCredentials getCredentials() {
                return new BasicAWSCredentials(awsAccessKey, awsSecretKey);
            }

            public void refresh() {

            }
        };
    }


    @Bean(name = "amazonSQS")
    public AmazonSQS amazonSQS(){
        AmazonSQS sqs = new AmazonSQSClient(awsCredentialsProvider());
        return sqs;
    }


    @Bean(name = "defaultMessageListenerContainer")
    public DefaultMessageListenerContainer defaultMessageListenerContainer(){
        DefaultMessageListenerContainer defaultMessageListenerContainer = new DefaultMessageListenerContainer();
        defaultMessageListenerContainer.setConcurrency("5");
        defaultMessageListenerContainer.setConcurrentConsumers(5);
        defaultMessageListenerContainer.setMaxConcurrentConsumers(2);
        defaultMessageListenerContainer.setIdleTaskExecutionLimit(30);
        defaultMessageListenerContainer.setIdleConsumerLimit(5);
        defaultMessageListenerContainer.setAutoStartup(true);
        defaultMessageListenerContainer.setDestinationName(destinationName);
        defaultMessageListenerContainer.setMessageListener(messageListenerAdapter());
        defaultMessageListenerContainer.setConnectionFactory(sqsConnectionFactory());
        return defaultMessageListenerContainer;
    }

    @Bean(name = "messageListenerAdapter")
    public MessageListenerAdapter messageListenerAdapter(){
        MessageListenerAdapter adapter = new MessageListenerAdapter(fileUploadMessageListener);
        adapter.setDefaultListenerMethod("onMessage");
        adapter.setMessageConverter(null);
        return adapter;
    }
}
