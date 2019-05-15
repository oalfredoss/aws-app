# Simple way to send an email using AWS SES from Spring Boot Application
A simple Java application illustrating usage of the AWS SDK SES for Java
## Requirements
To build and run these example you'll need:
* Gradle, you'll run the gradlew wrapper and all dependencies will be installed.
* An AWS account.
* AWS Credentials configured by setting the AWS_ACCESS_KEY_ID and AWS_SECRET_ACCESS_KEY environment variables.
* The default region used will be us-east-1.

To see more information about how to set AWS Credentials for use with AWS SDK for Java see http://docs.aws.amazon.com/sdk-for-java/v1/developer-guide/setup-credentials.html

## Prerequisites
You will need to go to IAM policies page and attach the  "AmazonSESFullAccess" policy to the user whose credentials have been configured.
Otherwise, you will likely get a `AmazonServiceException/Access Denied/403` error.

Also you need to verify the email to send and receive the message. To do this go to `SES Home/Email Addresses/Verify a New Email Address`
and type your email.

## Guide to send an email using AWS SDK SES with Spring Boot
### Gradle Dependencies
First, we need to add the dependencies to our build.gradle. We use JavaMail library.

    compile('org.springframework.boot:spring-boot-starter-mail')
    compile group: 'com.amazonaws', name: 'aws-java-sdk-ses', version: '1.11.538'

### Create a AmazonSimpleEmailService Bean 
This bean is used to get a connection with AWS SES

    @Configuration
    public class SesConfig {

      @Bean
      public AmazonSimpleEmailService amazonSimpleEmailService() {
        return AmazonSimpleEmailServiceClientBuilder
            .standard()
            .withRegion(Regions.US_EAST_1)
            .withCredentials(new DefaultAWSCredentialsProviderChain())
            .build();
      }
    }
### Create a SesService  to send the rawEmail
We write a @Service that uses `AmazonSimpleEmailService` to call the method sendRawEmail and send the email.

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

### Create a SesController to create a message and send it
In this simple Rest API,  we have an endpoint `/api/v1/sendEmail` that create a MimeMessage object from JavaMail library with the properties: SUBJECT, SENDER, RECIPIENT, BODY_HTML, ATTACHMENT_1 and ATTACHMENT_2. 
In the SesController we use `SesService` to send the email. The class `MimeMessageBuilder` is a helper used to create a MimeMessage object.
    
    @RestController
    @RequestMapping(path = "/api/v1")
    public class SesController {

      Logger logger = LoggerFactory.getLogger(SesController.class);
      private static final String SUBJECT = "Email Subject";
      private static final String SENDER = "user@mail.com";
      private static final String RECIPIENT = "user@mail.com"; 
      private static final String BODY_HTML ="<html><body>Hello from AWS SES<p></p></body></html>";
      private static final String ATTACHMENT_1 = "http://oalfredoss.com.s3-website.us-east-2.amazonaws.com/file1.pdf";
      private static final String ATTACHMENT_2 = "http://oalfredoss.com.s3-website.us-east-2.amazonaws.com/file2.pdf";

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
        Optional.ofNullable(message).ifPresent(m -> sesService.sendAwsRawEmail(m));
      }
    }

## Running the SES sample
This sample application connects to AWS SES (Simple Email Service), send an email with html body and attached pdf files.

In the file `src/main/java/com/example/api/controller/SesController.java` change the values SUBJECT, SENDER, RECIPIENT, BODY_HTML, ATTACHMENT_1, ATTACHMENT_2 for your own information. 

To build, open a command-line (terminal) window and change to the directory containing the example. Then type:

    ./gradlew clean build

To run, we need to run the JAR file by using the commnad given below,
    
    java -jar build/libs/<JAR>
    
To send the email execute the following command:
    
    curl http://localhost:8081/api/v1/sendEmail

Remember that the maximum size of the entire email message including pdf files is 10Mb.
