package com.oclock.oclock.security;

import com.oclock.oclock.dto.Member;
import com.oclock.oclock.exception.NotFoundException;
import com.oclock.oclock.model.Email;
import com.oclock.oclock.model.Role;
import com.oclock.oclock.service.MemberService;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;

import static org.springframework.security.core.authority.AuthorityUtils.createAuthorityList;

@Slf4j
@RequiredArgsConstructor
public class JwtAuthenticationProvider implements AuthenticationProvider {
    private final Jwt jwt;

    private final MemberService memberService;
    @Getter
    private String accessToken;
    @Getter
    private String refreshToken;





    @Override
    public boolean supports(Class<?> authentication) {
        return (JwtAuthenticationToken.class.isAssignableFrom(authentication));
    }

    /**
     * {@link org.springframework.security.authentication.ProviderManager#authenticate} 메소드에서 호출된다.
     *
     * null 이 아닌 값을 반환하면 인증 처리가 완료된다.
     */
    @Override
    public Authentication authenticate(Authentication authentication) throws AuthenticationException {
        JwtAuthenticationToken authenticationToken = (JwtAuthenticationToken) authentication;
        return processMemberAuthentication(authenticationToken.authenticationRequest());
    }

    private Authentication processMemberAuthentication(AuthenticationRequest request) {
        try {
            Member member = memberService.login(new Email(request.getEmail()), request.getPassword());
            JwtAuthenticationToken authenticated =
                    new JwtAuthenticationToken(new JwtAuthentication(member.getId()), null, createAuthorityList(Role.USER.value()));
            accessToken = member.newApiToken(jwt, new String[]{Role.USER.value()});
            refreshToken = member.newRefreshToken(jwt, new String[]{Role.USER.value()});
            authenticated.setDetails(new AuthenticationResult(accessToken, refreshToken));
            return authenticated;
        } catch (Exception e) {
            throw new NotFoundException(e.getMessage());
        }
    }
}
