package com.viniland.sales.domain.model;

import lombok.*;
import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.Transient;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;

import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Positive;
import java.math.BigDecimal;
import java.math.MathContext;
import java.util.Date;
import java.util.Set;

/**
 * Sale model
 */
@Document(collection = "sales")
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@EqualsAndHashCode(of = { "id" })
public class Sale {

    @Id
    private String id;

    @Field(value = "customer_id")
    @NotNull(message = "{sale.customer.null}")
    private Long customerId;

    @NotNull(message = "{sale.items.null}")
    @Min(1)
    @Singular
    private Set<SaleItem> items;

    @NotNull(message = "{sale.register.null}")
    private Date register;

    @Field(value = "total")
    @NotNull(message = "{sale.purchase.null}")
    @Positive(message = "{sale.purchase.value}")
    private Double total;

    @Field(value = "cashback")
    @NotNull(message = "{sale.cashback.null}")
    @Positive(message = "{sale.cashback.value}")
    private Double cashback;

    /**
     * Sale item model
     */
    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
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

        /**
         * Computes sale item total price
         *
         * @param context
         * @return {@link BigDecimal}
         */
        public BigDecimal computePrice(MathContext context) {
            return new BigDecimal(this.price, context).multiply(BigDecimal.valueOf(this.quantity));
        }

        /**
         * Computes sale item total cashback
         *
         * @param context
         * @return {@link BigDecimal}
         */
        public BigDecimal computeCashback(MathContext context) {
            return new BigDecimal(this.cashback, context).multiply(BigDecimal.valueOf(this.quantity));
        }
    }
}
