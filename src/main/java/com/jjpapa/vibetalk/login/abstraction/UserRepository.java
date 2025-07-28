package com.jjpapa.vibetalk.login.abstraction;

import com.jjpapa.vibetalk.login.domain.entity.User;
import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UserRepository  extends JpaRepository<User, Long> {
  Optional<User> findByEmail(String email);
  Optional<User> findByPhoneNumber(String phoneNumber);

  List<User> findByPhoneNumberIn(List<String> phoneNumbers);



}
