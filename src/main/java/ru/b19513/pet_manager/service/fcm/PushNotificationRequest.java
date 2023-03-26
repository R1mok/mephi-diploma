package ru.b19513.pet_manager.service.fcm;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Setter
@Getter
@NoArgsConstructor
public class PushNotificationRequest {

    private String title;
    private String message;
    private String topic;
    private String token;
    public PushNotificationRequest(String title, String messageBody, String topicName) {
        this.title = title;
        this.message = messageBody;
        this.topic = topicName;
    }
}
