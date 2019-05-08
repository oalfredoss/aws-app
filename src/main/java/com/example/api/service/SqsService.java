package com.example.api.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.cloud.aws.messaging.listener.SqsMessageDeletionPolicy;
import org.springframework.cloud.aws.messaging.listener.annotation.SqsListener;
import org.springframework.messaging.handler.annotation.Header;
import org.springframework.stereotype.Service;

import com.example.api.model.MessageModel;

@Service
public class SqsService {
  Logger logger = LoggerFactory.getLogger(SqsService.class);
  private static final String QUEUE = "awsqueue";
    
  @SqsListener(value = QUEUE, deletionPolicy = SqsMessageDeletionPolicy.ON_SUCCESS)
  public void receiveMessage(MessageModel message,  @Header("SenderId") String senderId) {
    logger.info("Received message: {}, having SenderId: {}", message, senderId);
  }
  
}
