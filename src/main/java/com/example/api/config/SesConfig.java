package com.example.api.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import com.amazonaws.auth.DefaultAWSCredentialsProviderChain;
import com.amazonaws.regions.Regions;
import com.amazonaws.services.simpleemail.AmazonSimpleEmailService;
import com.amazonaws.services.simpleemail.AmazonSimpleEmailServiceClientBuilder;

@Configuration
public class SesConfig {

  @Bean
  public AmazonSimpleEmailService amazonSimpleService() {
      return AmazonSimpleEmailServiceClientBuilder
          .standard()
          .withRegion(Regions.US_EAST_1)
          .withCredentials(new DefaultAWSCredentialsProviderChain())
          .build();
  }
}
