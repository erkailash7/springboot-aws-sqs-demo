package com.aws.sql.springbootawssqsdemo.controller;


import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cloud.aws.messaging.core.QueueMessagingTemplate;
import org.springframework.cloud.aws.messaging.listener.annotation.SqsListener;
import org.springframework.messaging.support.MessageBuilder;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;



@RestController
public class SqsController {

    Logger logger = LoggerFactory.getLogger(SqsController.class);

    @Autowired
    private QueueMessagingTemplate queueMessagingTemplate;

    @Value("${cloud.aws.end-point.uri}")
    private String endPoint;

    @Value("${cloud.aws.end-point-2.uri}")
    private String endPoint2;

    @GetMapping("/status")
    public String validate(){
        return "Application is running";
    }

    @GetMapping("/send/{message}")
    public String sendMessageToQueue(@PathVariable String message){
        queueMessagingTemplate.send(endPoint, MessageBuilder.withPayload(message).build());
        logger.info("Message sent in the queue of Process-1 : {}", message );
        return "Message sent in the queue of Process-1";
    }

    @SqsListener("kailash-queue")
    public void loadMessageFromSqs(String message){
        logger.info("Process-2 Reading the message from Process-1 queue : {}", message);
        logger.info("Process-2 sending the message to the Process-3 queue : {}", message);
        sendMessageToProcess2(message);
    }

   public void sendMessageToProcess2(String message){
        logger.info("Message sent in the queue of Process-2 : {}", message );
        queueMessagingTemplate.send(endPoint2, MessageBuilder.withPayload(message).build());
    }


}
