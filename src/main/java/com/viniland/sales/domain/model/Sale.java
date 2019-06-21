package com.viniland.sales.domain.model;

import lombok.*;
import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.Transient;
import org.springframework.data.mongodb.core.mapping.Document;

import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Positive;
import java.util.Date;
import java.util.Set;

/**
 * Sale model
 */
@Document(collection = "sales")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(of = { "id" })
public class Sale {

    @Id
    private Long id;

    @NotNull(message = "{sale.customer.null}")
    private Long customerId;

    @NotNull(message = "{sale.items.null}")
    @Min(1)
    private Set<SaleItem> items;

    @NotNull(message = "{sale.createdAt.null}")
    private Date createdAt;

    @Transient
    private Double totalPurchase;

    @Transient
    private Double totalCashback;

    /**
     * Sale item model
     */
    @EqualsAndHashCode(of = { "id" })
    public static class SaleItem {

        @NotNull(message = "{sale.item.id.null}")
        private String id;

        @NotNull(message = "{sale.item.price.null}")
        @Positive(message = "{sale.item.price.value}")
        private Double price;

        @NotNull(message = "{sale.item.cashback.null}")
        @Positive(message = "{sale.item.cashback.value}")
        private Double cashback;

        @NotNull(message = "{sale.item.quantity.null}")
        @Min(value = 1, message = "{sale.item.quantity.value}")
        private Integer quantity;
    }
}
