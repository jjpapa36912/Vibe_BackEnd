package com.jjpapa.vibetalk.chat.service;

import static org.junit.jupiter.api.Assertions.*;

import com.google.auth.oauth2.GoogleCredentials;
import com.google.firebase.FirebaseApp;
import com.google.firebase.FirebaseOptions;
import com.google.firebase.messaging.FirebaseMessaging;
import com.google.firebase.messaging.FirebaseMessagingException;
import com.google.firebase.messaging.Message;
import com.google.firebase.messaging.Notification;
import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.core.io.ClassPathResource;

@SpringBootTest
class PushNotificationServiceTest {

  @Autowired
  private PushNotificationService pushNotificationService;

  @BeforeEach
  public void setUp() throws Exception {
    if (FirebaseApp.getApps().isEmpty()) {
      GoogleCredentials credentials = GoogleCredentials
          .fromStream(new ClassPathResource("firebase-service-account.json").getInputStream())
          .createScoped(List.of("https://www.googleapis.com/auth/firebase.messaging"));

      FirebaseOptions options = FirebaseOptions.builder()
          .setCredentials(credentials)
          .build();

      FirebaseApp.initializeApp(options);
    }
  }

  @Test
  public void testFCM() throws FirebaseMessagingException {
    Message msg = Message.builder()
        .setToken("테스트_디바이스_토큰")
        .setNotification(Notification.builder()
            .setTitle("테스트")
            .setBody("이것은 테스트 메시지입니다")
            .build())
        .build();

    String response = FirebaseMessaging.getInstance().send(msg);
    System.out.println("푸시 테스트 성공: " + response);
  }


}