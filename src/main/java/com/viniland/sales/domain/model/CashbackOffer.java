package com.viniland.sales.domain.model;

import lombok.*;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.Map;

/**
 * Cashback offers model
 */
@Document(collection = "cashback_offers")
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@EqualsAndHashCode(of = { "id" })
public class CashbackOffer {

    @Id
    private WeekDay id;

    @Singular
    private Map<String, Double> values;

    /**
     * Week days
     */
    public enum WeekDay {

        SUNDAY, MONDAY, TUESDAY, WEDNESDAY, THURSDAY, FRIDAY, SATURDAY;

    }

}
