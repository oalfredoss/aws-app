package com.example.api.model;

public class MessageModel {
  
  private String messageId;
  private String body;

  public MessageModel(){}
   
  public String getMessageId() {
    return messageId;
  }
  public void setMessageId(String messageId) {
    this.messageId = messageId;
  }
  public String getBody() {
    return body;
  }
  public void setBody(String body) {
    this.body = body;
  }
  
  private MessageModel(Builder builder) {
    this.messageId = builder.messageId;
    this.body = builder.body;
  }
  /**
   * Creates builder to build {@link MessageModel}.
   * @return created builder
   */
  public static Builder builder() {
    return new Builder();
  }
  /**
   * Builder to build {@link MessageModel}.
   */
  public static final class Builder {
    private String messageId;
    private String body;

    private Builder() {
    }

    public Builder withMessageId(String messageId) {
      this.messageId = messageId;
      return this;
    }

    public Builder withBody(String body) {
      this.body = body;
      return this;
    }

    public MessageModel build() {
      return new MessageModel(this);
    }
  }
  
  @Override
  public String toString() {
    return "MessageModel [messageId=" + messageId + ", body=" + body + "]";
  }
  
} 
