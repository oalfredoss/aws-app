package com.example.api.controller;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import javax.mail.internet.MimeMessage;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.example.api.model.MimeMessageBuilder;
import com.example.api.service.SesService;

@RestController
@RequestMapping(path = "/api/v1")
public class SesController {
    Logger logger = LoggerFactory.getLogger(SesController.class);
    private static final String SUBJECT = "Email Subject";
    private static final String SENDER = "user@mail.com";
    private static final String RECIPIENT = "user@mail.com"; 
    private static final String BODY_HTML ="<html><body>Hello from AWS SES<p></p></body></html>";
    private static final String ATTACHMENT_1 = "https://github.com/oalfredoss/aws-app/blob/aws-ses/file1.pdf";
    private static final String ATTACHMENT_2 = "https://github.com/oalfredoss/aws-app/blob/aws-ses/file2.pdf";
    
    @Autowired
    SesService sesService;
    
    @GetMapping("/sendEmail")
    public void sendEmail() {
      List<String> attachments = new ArrayList<>();
      attachments.add(ATTACHMENT_1); attachments.add(ATTACHMENT_2);
      MimeMessage message = MimeMessageBuilder.builder()
          .withSubject(SUBJECT)
          .withSender(SENDER)
          .withRecipient(RECIPIENT)
          .withBodyHtml(BODY_HTML)
          .withAttachments(attachments)
          .build();
      Optional.ofNullable(message).ifPresent(m->sesService.sendAwsRawEmail(m));
    }
}
