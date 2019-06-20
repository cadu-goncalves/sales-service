package com.viniland.sales.component.security;

import com.viniland.sales.component.JwtProperties;
import io.jsonwebtoken.*;
import io.jsonwebtoken.security.SignatureException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.www.BasicAuthenticationFilter;
import org.springframework.util.StringUtils;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.List;
import java.util.stream.Collectors;

@Slf4j
public class JwtAuthorizationFilter extends BasicAuthenticationFilter {

    private final JwtProperties jwtProperties;

    public JwtAuthorizationFilter(AuthenticationManager authenticationManager, JwtProperties jwtProperties) {
        super(authenticationManager);
        this.jwtProperties = jwtProperties;
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response,
                                    FilterChain filterChain) throws IOException, ServletException {
        UsernamePasswordAuthenticationToken authentication = getAuthentication(request);
        if (authentication == null) {
            filterChain.doFilter(request, response);
        } else {
            SecurityContextHolder.getContext().setAuthentication(authentication);
            filterChain.doFilter(request, response);
        }
    }

    private UsernamePasswordAuthenticationToken getAuthentication(HttpServletRequest request) {
        // Build user credentials from JWT token
        UsernamePasswordAuthenticationToken result = null;
        String token = request.getHeader("Authorization");

        if (!StringUtils.isEmpty(token) && token.startsWith("Bearer")) {
            try {
                // Extract token clains
                byte[] signingKey = jwtProperties.getSecret().getBytes();
                Jws<Claims> claims = Jwts.parser()
                        .setSigningKey(signingKey)
                        .parseClaimsJws(token.replace("Bearer ", ""));

                // Extract credentias
                String username = claims.getBody().getSubject();
                List<SimpleGrantedAuthority> authorities = ((List<?>) claims.getBody()
                        .get("roles")).stream()
                        .map(authority -> new SimpleGrantedAuthority((String) authority))
                        .collect(Collectors.toList());

                if (!StringUtils.isEmpty(username)) {
                    result = new UsernamePasswordAuthenticationToken(username, null, authorities);
                }
            } catch (ExpiredJwtException exception) {
                log.warn("Expired JWT: {}\n", token, exception);
            } catch (UnsupportedJwtException exception) {
                log.warn("Unsupported JWT: {}\n", token, exception);
            } catch (MalformedJwtException exception) {
                log.warn("Invalid JWT: {}\n", token, exception);
            } catch (SignatureException exception) {
                log.warn("Invalid JWT signature: {}\n", token, exception);
            } catch (IllegalArgumentException exception) {
                log.warn("Empty JWT: {}\n", token, exception);
            }
        }

        return result;
    }
}
