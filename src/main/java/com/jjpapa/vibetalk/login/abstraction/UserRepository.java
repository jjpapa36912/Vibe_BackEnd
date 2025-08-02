package com.jjpapa.vibetalk.login.abstraction;

import com.jjpapa.vibetalk.login.domain.entity.User;
import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface UserRepository  extends JpaRepository<User, Long> {
  Optional<User> findByEmail(String email);
  Optional<User> findByPhoneNumber(String phoneNumber);

  List<User> findByPhoneNumberIn(List<String> phoneNumbers);
  @Modifying
  @Query("UPDATE User u SET u.deviceToken = NULL WHERE u.deviceToken = :token AND u.id <> :currentUserId")
  int clearTokenForOtherUsers(@Param("token") String token, @Param("currentUserId") Long currentUserId);
}


