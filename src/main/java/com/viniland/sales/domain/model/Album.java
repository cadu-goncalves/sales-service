package com.viniland.sales.domain.model;

import lombok.*;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Positive;
import java.util.Set;

/**
 * Album model
 */
@Document(collection = "albums")
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@EqualsAndHashCode(of = { "id" })
public class Album {

    @Id
    private String id;

    @NotNull(message = "{album.name.null}")
    private String name;

    @NotNull(message = "{album.genre.null}")
    private String genre;

    @NotNull(message = "{album.artists.null}")
    @Min(value = 1, message = "{album.artists.size}")
    private Set<String> artists;

    @NotNull(message = "{album.price.null}")
    @Positive(message = "{album.price.value")
    private Double price;

}
