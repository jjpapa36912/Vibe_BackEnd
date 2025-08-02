package com.jjpapa.vibetalk.chat.service;

import static com.google.cloud.MetadataConfig.getProjectId;

import com.google.auth.oauth2.GoogleCredentials;
import com.google.firebase.FirebaseApp;
import com.google.firebase.FirebaseOptions;
import com.google.firebase.messaging.FirebaseMessaging;
import com.google.firebase.messaging.FirebaseMessagingException;
import com.google.firebase.messaging.Message;
import com.google.firebase.messaging.Notification;
import jakarta.annotation.PostConstruct;
import java.io.FileInputStream;
import java.io.InputStream;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.io.ClassPathResource;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@Slf4j
public class PushNotificationService {

  @PostConstruct
  public void init() throws IOException {
    if (FirebaseApp.getApps().isEmpty()) {
      System.out.println("🚀 [PushNotificationService] Firebase 초기화 시작");

      InputStream serviceAccount =
          new ClassPathResource("firebase-service-account.json").getInputStream();


      FirebaseOptions options = FirebaseOptions.builder()
          .setCredentials(GoogleCredentials.fromStream(serviceAccount))
          .setProjectId("vibetalk-127a8") // 👈 반드시 추가
          .build();
      FirebaseApp.initializeApp(options);
      log.info("Firebase Project ID: {}",
          FirebaseApp.getInstance().getOptions().getProjectId());
      System.out.println("✅ [PushNotificationService] Firebase 초기화 완료");
    } else {
      System.out.println("ℹ️ [PushNotificationService] Firebase 이미 초기화됨");
    }
  }

  public void sendPushNotification(String token, String title, String body, Map<String, String> data) {
    System.out.println("📤 [PushNotificationService] 푸시 전송 시작");
    System.out.println("   ├─ 대상 토큰: " + token);
    System.out.println("   ├─ 제목: " + title);
    System.out.println("   ├─ 내용: " + body);
    System.out.println("   └─ 데이터: " + data);

    Message message = Message.builder()
        .setToken(token)
        .putAllData(data)
        .setNotification(Notification.builder()
            .setTitle(title)
            .setBody(body)
            .build())
        .build();
    log.info("🔔 [PushNotificationService] Target token: {}", token);
    log.info("📨 [PushNotificationService] Title: {}, Body: {}", title, body);
    try {
      String response = FirebaseMessaging.getInstance().send(message);
      log.info("✅ 푸시 전송 성공 - Message ID: {}", response);

    } catch (FirebaseMessagingException e) {
      log.error("❌ 푸시 전송 실패");
      log.error("   ├─ Error Code: {}", e.getErrorCode());           // INVALID_ARGUMENT, UNREGISTERED 등
      log.error("   ├─ Messaging Error Code: {}", e.getMessagingErrorCode()); // 자세한 코드 (v1 API)
      log.error("   ├─ HTTP Response: {}", e.getHttpResponse());     // 전체 HTTP 응답
      log.error("   ├─ Cause: {}", e.getCause() != null ? e.getCause().getMessage() : "N/A");
      log.error("   └─ Exception: ", e);
    }



  }
}
