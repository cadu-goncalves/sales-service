package com.viniland.sales.domain.rest;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonIgnore;
import io.swagger.annotations.ApiModel;
import lombok.*;

import javax.validation.constraints.Min;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import java.util.Date;
import java.util.Set;

/**
 * Sale resource
 */
@ApiModel
@Data
@NoArgsConstructor
@EqualsAndHashCode(of = "id")
public class SaleResource {

    @Getter(onMethod = @__( @JsonIgnore))
    @Setter
    private String id;

    @NotNull(message = "{sale.customer.null}")
    private Long customerId;

    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "dd/MM/yyyy hh:mm:ss")
    private Date register;

    @NotNull(message = "{sale.items.null}")
    @NotEmpty
    private Set<SaleItemResource> items;

    @Getter(onMethod = @__( @JsonIgnore))
    @Setter
    private Double totalPurchase;

    @Getter(onMethod = @__( @JsonIgnore))
    @Setter
    private Double totalCashback;


    /**
     * Sale item resource
     */
    @ApiModel
    @Data
    @NoArgsConstructor
    @EqualsAndHashCode(of = { "id" })
    public static class SaleItemResource {

        @NotNull(message = "{sale.item.id.null}")
        private String id;

        @NotNull(message = "{sale.item.quantity.null}")
        @Min(value = 1, message = "{sale.item.quantity.value}")
        private Integer quantity;

        @Getter(onMethod = @__( @JsonIgnore))
        @Setter
        private Double price;

    }

}
