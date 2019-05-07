package com.example.api.model;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.Collections;
import java.util.List;
import java.util.Properties;

import javax.activation.DataHandler;
import javax.activation.DataSource;
import javax.activation.URLDataSource;
import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.Session;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeBodyPart;
import javax.mail.internet.MimeMessage;
import javax.mail.internet.MimeMultipart;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class MimeMessageBuilder {
  private MimeMessageBuilder() {}
  /**
   * Creates builder to build {@link MimeMessageBuilder}.
   * @return created builder
   */
  public static Builder builder() {
    return new Builder();
  }
  /**
   * Builder to build {@link MimeMessageBuilder}.
   */
  public static final class Builder {
    
    Logger logger = LoggerFactory.getLogger(MimeMessageBuilder.class);
    
    private String subject;
    private String sender;
    private String recipient;
    private String bodyHtml;
    private List<String> attachments = Collections.emptyList();

    private Builder() {
    }

    public Builder withSubject(String subject) {
      this.subject = subject;
      return this;
    }

    public Builder withSender(String sender) {
      this.sender = sender;
      return this;
    }

    public Builder withRecipient(String recipient) {
      this.recipient = recipient;
      return this;
    }

    public Builder withBodyHtml(String bodyHtml) {
      this.bodyHtml = bodyHtml;
      return this;
    }

    public Builder withAttachments(List<String> attachments) {
      this.attachments = attachments;
      return this;
    }


    public void attachUrlFile(MimeMultipart part, String urlFile) {

      MimeBodyPart attachment = new MimeBodyPart();
      try {
        URL url = new URL(urlFile);
        DataSource source = new URLDataSource(url);
        attachment.setDataHandler(new DataHandler(source));
        attachment.setFileName(source.getName());
        part.addBodyPart(attachment);
      } catch (MalformedURLException | MessagingException e) {
        logger.error("Cannot get file from {} , check the error {}", urlFile, e.getMessage());
        
      }

    }
    
    public MimeMessage build() {
      
      MimeMessage message = null;
      try {
        Session session = Session.getDefaultInstance(new Properties());
        message = new MimeMessage(session);
        message.setSubject(this.subject, "UTF-8");
        message.setFrom(new InternetAddress(this.sender));
        message.setRecipients(Message.RecipientType.TO, InternetAddress.parse(this.recipient));
                
        MimeBodyPart htmlPart = new MimeBodyPart();
        htmlPart.setContent(this.bodyHtml,"text/html; charset=UTF-8");
        
        MimeMultipart messageBody = new MimeMultipart("alternative"); 
        messageBody.addBodyPart(htmlPart);
        // Create a wrapper for the HTML part.
        MimeBodyPart wrap = new MimeBodyPart();
        wrap.setContent(messageBody);
        
        // Create a multipart/mixed parent container.
        MimeMultipart multipart = new MimeMultipart("mixed");
        multipart.addBodyPart(wrap);
        this.attachments.stream().forEach(url->attachUrlFile(multipart, url));
        
        message.setContent(multipart);
      } catch (MessagingException e) {
        logger.error("Error to create MimeMessage object, see the error {}", e.getMessage());
      }
      return message;
    }
  }
  
}