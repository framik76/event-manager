package com.yanchware.eventmanager.eventmanager.service;

import com.google.api.core.ApiFuture;
import com.google.cloud.pubsub.v1.Publisher;
import com.google.protobuf.ByteString;
import com.google.pubsub.v1.PubsubMessage;
import com.google.pubsub.v1.TopicName;
import com.yanchware.eventmanager.eventmanager.entity.Event;
import com.yanchware.eventmanager.eventmanager.model.EventMessage;
import com.yanchware.eventmanager.eventmanager.repository.EventRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.sql.Timestamp;
import java.util.List;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;

@Slf4j
@Service
@RequiredArgsConstructor
public class EventService {

    @Value("${project.id}")
    String projectId;
    @Value("${topic.id}")
    String topicId;

    private final EventRepository eventRepository;

    public void sendEvent(EventMessage eventMessage) throws IOException, ExecutionException, InterruptedException {
        TopicName topicName = TopicName.of(projectId, topicId);

        Publisher publisher = null;
        try {
            publisher = Publisher.newBuilder(topicName).build();
            String message = EventMessage.toJSON(eventMessage);
            ByteString data = ByteString.copyFromUtf8(message);
            PubsubMessage pubsubMessage = PubsubMessage.newBuilder().setData(data).build();
            // Once published, returns a server-assigned message id (unique within the topic)
            ApiFuture<String> messageIdFuture = publisher.publish(pubsubMessage);
            String messageId = messageIdFuture.get();
            log.info("Published message ID: " + messageId);
        } finally {
          if (publisher != null) {
            publisher.shutdown();
            publisher.awaitTermination(1, TimeUnit.MINUTES);
          }
        }
    }

    public void saveEvent(String messageData) {
        var jsonObject = new JSONObject(messageData);
        var event = new Event();
        event.setOperation(jsonObject.getString("operation"));
        event.setType(jsonObject.getString("type"));
        event.setUserId(jsonObject.getLong("userId"));
        event.setTimestamp(new Timestamp(jsonObject.getLong("timestamp")));
        eventRepository.save(event);
    }

    public List<Event> getEventsByUserIdAndType(long userId, String type) {
        return eventRepository.findEventByUserIdAndType(userId, type);
    }

}
