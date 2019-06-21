package com.viniland.sales.component.spotify;

import com.viniland.sales.component.SpotifyProperties;
import com.viniland.sales.component.SpringContext;
import com.wrapper.spotify.SpotifyApi;
import com.wrapper.spotify.exceptions.SpotifyWebApiException;
import com.wrapper.spotify.model_objects.credentials.ClientCredentials;
import com.wrapper.spotify.requests.authorization.client_credentials.ClientCredentialsRequest;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.io.IOException;

@Component
@Slf4j
public class SpotifyClient {

    public static SpotifyApi getInstance() {
        SpotifyProperties spotifyProperties = SpringContext.getBean(SpotifyProperties.class);

        SpotifyApi api = new SpotifyApi.Builder()
                .setClientId(spotifyProperties.getId())
                .setClientSecret(spotifyProperties.getSecret())
                .build();
        ClientCredentialsRequest request = api.clientCredentials().build();

        try {
            final ClientCredentials clientCredentials = request.execute();
            api.setAccessToken(clientCredentials.getAccessToken());
            log.info("Spotify access token expires in {}", clientCredentials.getExpiresIn());
            return api;
        } catch (IOException | SpotifyWebApiException e) {
           log.error("Spotify access failed", e);
           return null;
        }
    }

}