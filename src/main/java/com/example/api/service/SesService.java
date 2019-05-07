package com.example.api.service;

import java.io.ByteArrayOutputStream;
import java.nio.ByteBuffer;

import javax.mail.internet.MimeMessage;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.amazonaws.services.simpleemail.AmazonSimpleEmailService;
import com.amazonaws.services.simpleemail.model.RawMessage;
import com.amazonaws.services.simpleemail.model.SendRawEmailRequest;
import com.amazonaws.services.simpleemail.model.SendRawEmailResult;

@Service
public class SesService {
  
  Logger logger = LoggerFactory.getLogger(SesService.class);
  
  @Autowired
  AmazonSimpleEmailService sesClient;
  
  public void sendAwsRawEmail(MimeMessage message) {
    
    try {
      logger.info("Send an email through AWS SES using the AWS SDK for Java ...");
      ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
      message.writeTo(outputStream);
      RawMessage rawMessage = new RawMessage(ByteBuffer.wrap(outputStream.toByteArray()));

      SendRawEmailRequest rawEmailRequest = new SendRawEmailRequest(rawMessage);
      SendRawEmailResult response = sesClient.sendRawEmail(rawEmailRequest);

      logger.info("Email sent: {}", response);
    } catch (Exception e) {
      logger.error("Email Failed \n Error message: {}", e.getMessage());
    }
  }
}
