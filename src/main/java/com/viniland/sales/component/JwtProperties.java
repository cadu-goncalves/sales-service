package com.viniland.sales.component;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

import java.util.Map;

/**
 * JWT configurations mapping
 */
@Component
@Data
@ConfigurationProperties("jwt")
public class JwtProperties {

    private String secret;

    private String auth;

    private Map<String, Object> claims;
}
