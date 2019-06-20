package com.viniland.sales.component.security;

import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.AuthenticationServiceException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.authority.AuthorityUtils;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

/**
 * Custom authentication for Spring Security
 *
 * In real ecosystem, it will be implemented on a external OAuth / UAA service
 */
@Component
public class AuthProvider implements AuthenticationProvider {

    @Override
    public boolean supports(Class<?> authentication) {
        return authentication.equals(UsernamePasswordAuthenticationToken.class);
    }

    protected String getLogin(final Authentication authentication) {
        final String login = authentication.getPrincipal().toString();
        if (StringUtils.isEmpty(login)) {
            throw new AuthenticationServiceException("user required");
        }
        return login;
    }

    protected String getPassword(final Authentication authentication) {
        final String password = authentication.getCredentials().toString();
        if (StringUtils.isEmpty(password)) {
            throw new AuthenticationServiceException("password required");
        }
        return password;
    }


    @Override
    public Authentication authenticate(Authentication authentication) throws AuthenticationException {
        String username = getLogin(authentication);
        String password = getPassword(authentication);

        try {
            /*
                DISCLAIMER:

                Fake authorization since no auth requirements where defined, in real
                implementation external OAuth / UAA server will take place
             */

            Optional<User> user = Optional.of(new User("dummy", "pwd", Arrays.asList("role_1", "role_2")));
            if(user.isPresent()){
                // Granted
                List<String> roles = user.get().getRoles();
                String[] auth = new String[roles.size()];
                roles.toArray(auth);
                return new UsernamePasswordAuthenticationToken(username, password,
                        AuthorityUtils.createAuthorityList(auth));

            } else {
                // Denied
                return null;
            }
        } catch (Exception e) {
            throw new AuthenticationServiceException(e.getMessage());
        }
    }
}
