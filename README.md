# Simple way to send and receive messages using AWS SQS from Spring Boot Application
A simple Java application illustrating usage of the AWS SQS and Spring Cloud AWS Messaging for Java
## Requirements
To build and run these example you'll need:
* Gradle, you'll run the gradlew wrapper and all dependencies will be installed.
* An AWS account.
* AWS Credentials configured by setting the AWS_ACCESS_KEY_ID and AWS_SECRET_ACCESS_KEY environment variables.
* The default region used will be us-east-1.

To see more information about how to set AWS Credentials for use with AWS SDK for Java see http://docs.aws.amazon.com/sdk-for-java/v1/developer-guide/setup-credentials.html

## Prerequisites
You will need to go to IAM policies page and attach the  "AmazonSQSFullAccess" policy to the user whose credentials have been configured.
Otherwise, you will likely get a `AmazonServiceException/Access Denied/403` error.

Also you need to create a queue on AWS, to do this go to [Amazon SQS console](https://console.aws.amazon.com/sqs/) click on `Create New Queue`
and type the `Queue Name` and choose `Standard`, finally choose `Quick-Create Queue`.

## Guide to send an receive a message using AWS SQS with Spring Boot
### Gradle Dependencies
First, we need to add the dependencies to our build.gradle. We use Spring Cloud AWS Messaging library.

    compile group: 'org.springframework.cloud', name: 'spring-cloud-aws-messaging', version: '2.1.1.RELEASE'

### Create a QueueMessagingTemplate and AmazonSQSAsync Beans 
The AmazonSQSAsync bean is used to get a connection with AWS SQS, the QueueMessagingTemplate is used to send a message and SimpleMessageListenerContainerFactory is used to configure the listener which will be receiving messages. The annotation @EnableSqs enable to listener methods create a listener objects. If you just want to send a message the SimpleMessageListenerContainerFactory bean and  @EnableSqs annotation are not required.
    
    @EnableSqs
    @Configuration
    public class SqsConfig {

      @Bean
      public QueueMessagingTemplate queueMessagingTemplate() {
          return new QueueMessagingTemplate(amazonSQS());
      }

      @Bean
      public SimpleMessageListenerContainerFactory simpleMessageListenerContainerFactory() {
          SimpleMessageListenerContainerFactory factory = new SimpleMessageListenerContainerFactory();
          factory.setAmazonSqs(amazonSQS());
          factory.setMaxNumberOfMessages(1);
          factory.setVisibilityTimeout(10);
          return factory;
      }

      @Bean
      public AmazonSQSAsync amazonSQS() {
        AmazonSQSAsync amazonSQSAsyncClient = AmazonSQSAsyncClient
            .asyncBuilder()
            .withRegion(Regions.US_EAST_1)
            .withCredentials(new DefaultAWSCredentialsProviderChain())
            .build();
        return new AmazonSQSBufferedAsyncClient(amazonSQSAsyncClient);
      }
    }
### Define an MessageModel that will be send and receive
We write the following class with the attributes messageId and body. This is the structure that  QueueMessagingTemplate will convert to json and
send to the queue.
    
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

      ...

    } 

### Create a SqSService to receive a message
We write a `receiveMessage` listener annotate method to reveiving SQS messages. The SQS message will be delete when successfully executed by the listener method.
 If an exception is thrown by the listener method, the message will not be deleted.
  The @SqsListener enables the convertion from json string to MessageModel object automatically. 

    @Service
    public class SqsService {
      Logger logger = LoggerFactory.getLogger(SqsService.class);
      private static final String QUEUE = "awsqueue";

      @SqsListener(value=QUEUE, deletionPolicy = SqsMessageDeletionPolicy.ON_SUCCESS)
      public void receiveMessage(MessageModel message,  @Header("SenderId") String senderId) {
        logger.info("Received message: {}, having SenderId: {}", message, senderId);
      }

    }

### Create a SqsController to send a message
In this simple Rest API,  we have an endpoint `/api/v1/sendMessage` that create a MessageModel object. 
In the SqsController we use `QueueMessagingTemplate` to convert and send the message.
    
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

## Running the SQS sample
This sample application creates a listener to receiving messages from AWS SQS (Simple Queue Service), and defines and endpoint to send a message to SQS queue.

In the file `... api/controller/SqsController.java` and  `... api/service/SqsService.java` change the value `QUEUE` for your queue name. 

To build, open a command-line (terminal) window and change to the directory containing the example. Then type:

    ./gradlew clean build

To run, we need to run the JAR file by using the commnad given below,
    
    java -jar build/libs/<JAR>
    
To send the message execute the following command:
    
    curl http://localhost:8081/api/v1/sendMessage

As soon as you send the message the listener receives it. In the log you should see when the listener receives the message.

