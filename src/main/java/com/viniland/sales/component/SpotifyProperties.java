package com.viniland.sales.component;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

/**
 * Spotify configurations mapping
 */
@Component
@Data
@ConfigurationProperties("spotify")
public class SpotifyProperties {

    private String id;

    private String secret;

}
