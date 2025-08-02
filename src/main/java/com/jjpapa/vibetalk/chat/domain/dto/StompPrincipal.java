package com.jjpapa.vibetalk.chat.domain.dto;

import java.security.Principal;

public class StompPrincipal implements Principal {
  private final String name;

  public StompPrincipal(String name) {
    this.name = name;
  }

  @Override
  public String getName() {
    return name;
  }
}
