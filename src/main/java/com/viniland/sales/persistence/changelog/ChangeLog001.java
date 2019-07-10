package com.viniland.sales.persistence.changelog;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.github.mongobee.changeset.ChangeLog;
import com.github.mongobee.changeset.ChangeSet;
import com.viniland.sales.component.spotify.SpotifyClient;
import com.viniland.sales.domain.model.Album;
import com.viniland.sales.domain.model.CashbackOffer;
import com.wrapper.spotify.SpotifyApi;
import lombok.extern.slf4j.Slf4j;
import org.jongo.Jongo;
import org.jongo.MongoCollection;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.util.ResourceUtils;

import java.io.File;
import java.io.IOException;
import java.math.BigDecimal;
import java.math.MathContext;
import java.math.RoundingMode;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Objects;
import java.util.Set;

/**
 * Database change logs 001
 */
@ChangeLog(order = "001")
@Slf4j
public class ChangeLog001 {

    public static final MathContext MATH_CONTEXT = new MathContext(4, RoundingMode.HALF_UP);

    /**
     * Import cashbacks offers
     *
     * @param jongo {@link Jongo}
     */
    @ChangeSet(order = "001", id = "Import cashback offers", author = "cadu.goncalves")
    public void importOffers(Jongo jongo) {
        String name = CashbackOffer.class.getAnnotation(Document.class).collection();
        MongoCollection collection = jongo.getCollection(name);

        try {
            File file = ResourceUtils.getFile("classpath:changelog/001/cashback_offer.json");
            ArrayNode data = (ArrayNode) new ObjectMapper().readTree(file);
            data.elements().forEachRemaining(collection::insert);
        } catch (IOException ioe) {
            log.error("Unable to perform migration", ioe);
            throw new RuntimeException(ioe);
        }
    }

    /**
     * Import albums
     *
     * @param jongo {@link Jongo}
     */
    @ChangeSet(order = "002", id = "Import albums from Spotify", author = "cadu.goncalves")
    public void importAlbuns(Jongo jongo) {
        String name = Album.class.getAnnotation(Document.class).collection();
        MongoCollection collection = jongo.getCollection(name);

        SpotifyApi api = SpotifyClient.getInstance();
        if(Objects.isNull(api)) {
            // Ignore and move on
            return;
        }

        Arrays.asList("rock", "mpb", "classical", "pop").forEach(genre -> {
            try {
                // Bypass wrong result mapping by using the raw JSON
                String raw = api.getRecommendations().seed_genres(genre).limit(50).build().getJson();

                // Import
                JsonNode jsonRecommendations = new ObjectMapper().readTree(raw);
                jsonRecommendations.get("tracks").elements().forEachRemaining(track -> {
                    // Random price
                    BigDecimal randomPrice = new BigDecimal((Math.random() + 1) * 25.8136, MATH_CONTEXT);

                    // Album
                    JsonNode jsonAlbum = track.get("album");
                    Album album = Album.builder()
                            .name(jsonAlbum.get("name").asText())
                            .genre(genre)
                            .price(randomPrice.doubleValue())
                            .build();

                    // Artist
                    Set<String> artists = new HashSet<>();
                    jsonAlbum.get("artists").elements().forEachRemaining(artist ->
                        artists.add(artist.get("name").textValue())
                    );
                    album.setArtists(artists);

                    collection.insert(album);
                });

            } catch (Exception ioe) {
                log.error("Unable to perform migration", ioe);
                throw new RuntimeException(ioe);
            }
        });
    }
}
