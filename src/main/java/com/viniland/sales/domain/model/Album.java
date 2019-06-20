package com.viniland.sales.domain.model;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;

import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Positive;
import java.util.Set;

/**
 * Album model
 */
@Document(collection = "albums")
@Data
@NoArgsConstructor
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
public class Album {

    @Id
    @EqualsAndHashCode.Include
    private String id;

    @Field
    @NotNull(message = "{album.name.null}")
    private String name;

    @Field
    @NotNull(message = "{album.tracks.null}")
    @Min(value = 1, message = "{album.tracks.size}")
    private Set<Track> tracks;

    @Field
    @NotNull(message = "{album.genrers.null}")
    @Min(value = 1, message = "{album.genres.size}")
    private Set<String> genres;

    @Field
    @NotNull(message = "{album.artists.null}")
    @Min(value = 1, message = "{album.artists.size}")
    private Set<String> artists;

    @Field
    @NotNull(message = "{album.price.null}")
    @Positive(message = "{album.price.value")
    private Double currentPrice;


    /**
     * Album track model
     */
    @Data
    @NoArgsConstructor
    @EqualsAndHashCode(onlyExplicitlyIncluded = true)
    public static class Track {

        @Field
        @Min(value = 1, message = "{track.number.value}")
        @NotNull(message = "{track.number.null}")
        @EqualsAndHashCode.Include
        private Integer number;

        @Field
        @NotNull(message = "{track.name.null}")
        private String name;

        @Field
        @Min(value = 0, message = "{track.duration.value}")
        @NotNull(message = "{track.duration.null}")
        private Integer duration;


    }
}
