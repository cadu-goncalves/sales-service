package com.viniland.sales.config;

import com.viniland.sales.component.JwtProperties;
import com.viniland.sales.component.security.AuthProvider;
import com.viniland.sales.component.security.JwtAuthenticationFilter;
import com.viniland.sales.component.security.JwtAuthorizationFilter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity;
import org.springframework.security.config.annotation.method.configuration.GlobalMethodSecurityConfiguration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;

/**
 * Bootstrap security configurations.
 */
@EnableWebSecurity
@EnableGlobalMethodSecurity(securedEnabled = true, prePostEnabled = true)
public class WebSecurityConfig extends GlobalMethodSecurityConfiguration {

    public static final String BASE_URI = "/api/**/";

    /**
     * API Web security filters and policies (unrestricted)
     */
    @Configuration
    @Profile("no-auth")
    public static class UnrestrictedApiWebSecurityConfig extends WebSecurityConfigurerAdapter {

        @Autowired
        private AuthProvider authProvider;

        @Autowired
        private JwtProperties jwtProperties;

        protected void configure(HttpSecurity http) throws Exception {
            // @formatter:off

            // Auth rules
            http.antMatcher(BASE_URI).authorizeRequests()
                .anyRequest().permitAll()
                .and()
                // JWT
                .addFilter(new JwtAuthenticationFilter(authenticationManager(), jwtProperties))
                .addFilter(new JwtAuthorizationFilter(authenticationManager(), jwtProperties));

            // Disable CSRF filter
            http.csrf()
                .disable();

            // Session management
            http.sessionManagement()
                .sessionCreationPolicy(SessionCreationPolicy.STATELESS);

            // @formatter:om
        }

        @Override
        protected void configure(AuthenticationManagerBuilder auth) {
            // Attach auth provider to Spring Security
            auth.authenticationProvider(authProvider);
        }

    }

    /**
     * API Web security filters and policies
     */
    @Configuration
    public static class ApiWebSecurityConfig extends WebSecurityConfigurerAdapter {

        @Autowired
        private AuthProvider authProvider;

        @Autowired
        private JwtProperties jwtProperties;

        protected void configure(HttpSecurity http) throws Exception {
            // @formatter:off

            // Auth rules
            http.antMatcher(BASE_URI).authorizeRequests()
                .anyRequest().authenticated()
                .and()
                // JWT
                .addFilter(new JwtAuthenticationFilter(authenticationManager(), jwtProperties))
                .addFilter(new JwtAuthorizationFilter(authenticationManager(), jwtProperties));

            // Disable CSRF filter
            http.csrf()
                .disable();

            // Session management
            http.sessionManagement()
                .sessionCreationPolicy(SessionCreationPolicy.STATELESS);

            // @formatter:om
        }

        @Override
        protected void configure(AuthenticationManagerBuilder auth) {
            // Attach auth provider to Spring Security
            auth.authenticationProvider(authProvider);
        }

    }


    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

}
