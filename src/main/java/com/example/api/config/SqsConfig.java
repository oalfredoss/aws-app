package com.example.api.config;

import org.springframework.cloud.aws.messaging.config.SimpleMessageListenerContainerFactory;
import org.springframework.cloud.aws.messaging.config.annotation.EnableSqs;
import org.springframework.cloud.aws.messaging.core.QueueMessagingTemplate;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import com.amazonaws.auth.DefaultAWSCredentialsProviderChain;
import com.amazonaws.regions.Regions;
import com.amazonaws.services.sqs.AmazonSQSAsync;
import com.amazonaws.services.sqs.AmazonSQSAsyncClient;
import com.amazonaws.services.sqs.buffered.AmazonSQSBufferedAsyncClient;

@EnableSqs
@Configuration
public class SqsConfig {
  
  @Bean
  public QueueMessagingTemplate queueMessagingTemplate() {
      return new QueueMessagingTemplate(amazonSqs());
  }
  
  @Bean
  public SimpleMessageListenerContainerFactory simpleMessageListenerContainerFactory() {
      SimpleMessageListenerContainerFactory factory = new SimpleMessageListenerContainerFactory();
      factory.setAmazonSqs(amazonSqs());
      factory.setMaxNumberOfMessages(1);
      factory.setVisibilityTimeout(10);
      return factory;
  }
  
  @Bean
  public AmazonSQSAsync amazonSqs() {
    AmazonSQSAsync amazonSQSAsyncClient = AmazonSQSAsyncClient
        .asyncBuilder()
        .withRegion(Regions.US_EAST_1)
        .withCredentials(new DefaultAWSCredentialsProviderChain())
        .build();
    return new AmazonSQSBufferedAsyncClient(amazonSQSAsyncClient);
  }
}
