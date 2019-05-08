package com.example.api.controller;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cloud.aws.messaging.core.QueueMessagingTemplate;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import com.example.api.model.MessageModel;
import static org.springframework.http.HttpStatus.OK;

@RestController
@RequestMapping(path = "/api/v1")
public class SqsController {
    Logger logger = LoggerFactory.getLogger(SqsController.class);
    private static final String QUEUE = "awsqueue";
    
    @Autowired
    QueueMessagingTemplate queueMessagingTemplate;
    
    @GetMapping("/sendMessage")
    @ResponseStatus(OK)
    public void sendMessage() {
      MessageModel message = MessageModel.builder()
          .withMessageId("9999")
          .withBody("Some information to send")
          .build();
      logger.info("Sending message to SQS queue");
      queueMessagingTemplate.convertAndSend(QUEUE, message);
    }
}
