package com.oclock.oclock.dto;

import lombok.Builder;
import lombok.Getter;

@Getter
public class RefreshRequest {
  String refreshToken;

  public RefreshRequest() {
  }
}
