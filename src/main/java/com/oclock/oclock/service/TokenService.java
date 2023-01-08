package com.oclock.oclock.service;

import com.oclock.oclock.dto.RefreshRequest;
import com.oclock.oclock.repository.RefreshTokenRepository;
import com.oclock.oclock.security.Jwt;
import com.oclock.oclock.security.RefreshToken;
import com.oclock.oclock.utils.TokenToID;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.util.List;

@Service
@RequiredArgsConstructor
public class TokenService {
  @Autowired
  RefreshTokenRepository repository;

  @Autowired
  private Jwt jwt;
  public boolean isValidRefreshToken(RefreshRequest request) {
    String token = request.getRefreshToken();
    Long memberId = TokenToID.getIdFromToken(token, jwt);
    List<RefreshToken> tokens = repository.findByRefreshToken(token);
    if (tokens.isEmpty()) return false;
    RefreshToken refreshToken = tokens.get(0);
    return refreshToken.getId() == memberId ? true : false;
  }
}
