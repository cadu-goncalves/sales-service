package com.viniland.sales.domain.rest;

import io.swagger.annotations.ApiModel;
import lombok.*;

import java.util.Set;

/**
 * Album resource
 */
@ApiModel
@Data
@NoArgsConstructor
@EqualsAndHashCode(of = "id")
public class AlbumResource {

    private String id;

    private String name;

    private String genre;

    private Set<String> artists;

    private Double price;
}
