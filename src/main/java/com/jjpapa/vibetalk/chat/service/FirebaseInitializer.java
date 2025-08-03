//package com.jjpapa.vibetalk.chat.service;
//
//import com.google.auth.oauth2.GoogleCredentials;
//import com.google.firebase.FirebaseApp;
//import com.google.firebase.FirebaseOptions;
//
//import jakarta.annotation.PostConstruct;
//import org.springframework.stereotype.Service;
//
//import java.io.FileInputStream;
//import java.io.IOException;
//
//@Service
//public class FirebaseInitializer {
//
//  @PostConstruct
//  public void initialize() throws IOException {
//    FileInputStream serviceAccount =
//        new FileInputStream("src/main/resources/firebase-service-account.json");
//
//    FirebaseOptions options = FirebaseOptions.builder()
//        .setCredentials(GoogleCredentials.fromStream(serviceAccount))
//        .setProjectId("vibetalk-127a8")  // 반드시 Firebase 프로젝트 ID와 동일
//        .build();
//
//    if (FirebaseApp.getApps().isEmpty()) {
//      FirebaseApp.initializeApp(options);
//    }
//
//  }
//}
