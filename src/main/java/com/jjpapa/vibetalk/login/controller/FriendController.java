package com.jjpapa.vibetalk.login.controller;

import com.jjpapa.vibetalk.login.abstraction.UserRepository;
import com.jjpapa.vibetalk.login.domain.dto.ContactSyncRequest;
import com.jjpapa.vibetalk.login.domain.dto.FriendSyncResponse;
import com.jjpapa.vibetalk.login.domain.entity.User;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/friends")
@RequiredArgsConstructor
public class FriendController {

  private final UserRepository userRepository;

  @PostMapping("/sync")
  public ResponseEntity<List<FriendSyncResponse>> syncContacts(@RequestBody List<ContactSyncRequest> contacts) {
    List<String> numbers = contacts.stream()
        .map(ContactSyncRequest::getPhoneNumber)
        .toList();

    List<User> users = userRepository.findByPhoneNumberIn(numbers);

    List<FriendSyncResponse> response = users.stream()
        .map(user -> {
          String contactName = contacts.stream()
              .filter(c -> c.getPhoneNumber().equals(user.getPhoneNumber()))
              .map(ContactSyncRequest::getContactName)
              .findFirst()
              .orElse(user.getName());

          return new FriendSyncResponse(
              user.getId(),
              user.getPhoneNumber(),
              user.getName(),
              contactName,
              user.getStatusMessage(),
              user.getProfileImageUrl()
          );
        })
        .toList();

    return ResponseEntity.ok(response);
  }
}