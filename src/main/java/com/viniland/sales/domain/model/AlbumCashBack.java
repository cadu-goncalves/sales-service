package com.viniland.sales.domain.model;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.Map;

/**
 * Album cashback model
 */
@Document(collection = "albums_cashback")
@Data
@NoArgsConstructor
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
public class AlbumCashBack {

    @Id
    @EqualsAndHashCode.Include
    private WeekDay id;

    private Map<String, Double> values;

    public static enum WeekDay {

        SUNDAY, MONDAY, TUESDAY, WEDNESDAY, THURSDAY, FRIDAY, SATURDAY;

    }

}
