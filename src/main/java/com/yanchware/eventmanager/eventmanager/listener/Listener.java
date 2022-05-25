package com.yanchware.eventmanager.eventmanager.listener;

import com.google.cloud.pubsub.v1.AckReplyConsumer;
import com.google.cloud.pubsub.v1.MessageReceiver;
import com.google.cloud.pubsub.v1.Subscriber;
import com.google.pubsub.v1.ProjectSubscriptionName;
import com.google.pubsub.v1.PubsubMessage;
import com.yanchware.eventmanager.eventmanager.service.EventService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

@Slf4j
@Component
@RequiredArgsConstructor
public class Listener {

    private final EventService eventService;

    @Autowired
    public Listener(@Value("${project.id}") String projectId,
                    @Value("${topic.id}") String topicId,
                    @Value("${subscription.id}") String subscriptionId,
                    EventService eventService
                    ) {
        this.eventService = eventService;
        ProjectSubscriptionName subscriptionName = ProjectSubscriptionName.of(projectId, subscriptionId);

        // Instantiate an asynchronous message receiver.
        MessageReceiver receiver =
            (PubsubMessage message, AckReplyConsumer consumer) -> {
            // Handle incoming message, then ack the received message.
            log.info("Id: " + message.getMessageId());
            var messageData = message.getData().toStringUtf8();
            log.info("Data: " + messageData);
            eventService.saveEvent(messageData);
            consumer.ack();
        };

        Subscriber subscriber = null;
        try {
          subscriber = Subscriber.newBuilder(subscriptionName, receiver).build();
          // Start the subscriber.
          subscriber.startAsync().awaitRunning();
          log.info("Listening for messages on %s:\n", subscriptionName.toString());
          // Allow the subscriber to run for 30s unless an unrecoverable error occurs.
          subscriber.awaitTerminated(30, TimeUnit.SECONDS);
        } catch (TimeoutException timeoutException) {
          // Shut down the subscriber after 30s. Stop receiving messages.
          subscriber.stopAsync();
        }

    }
}
