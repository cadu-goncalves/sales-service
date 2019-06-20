package com.viniland.sales.domain.model;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

/**
 * Sale model
 */
@Document(collection = "sales")
@Data
@NoArgsConstructor
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
public class Sale {

    @Id
    @EqualsAndHashCode.Include
    private Long id;

    private Long customerId;
}
