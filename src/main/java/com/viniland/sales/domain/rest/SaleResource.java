package com.viniland.sales.domain.rest;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.annotations.ApiModel;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

import javax.validation.constraints.Min;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.PastOrPresent;
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

    @JsonProperty(access = JsonProperty.Access.READ_ONLY)
    private String id;

    @NotNull(message = "{sale.customer.null}")
    private Long customerId;

    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "dd/MM/yyyy HH:mm:ss")
    @PastOrPresent(message = "{sale.register.invalid}")
    private Date register;

    @NotNull(message = "{sale.items.null}")
    @NotEmpty
    private Set<SaleItemResource> items;

    @JsonProperty(access = JsonProperty.Access.READ_ONLY)
    private Double total;

    @JsonProperty(access = JsonProperty.Access.READ_ONLY)
    private Double cashback;


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

        @JsonProperty(access = JsonProperty.Access.READ_ONLY)
        private Double price;

        @JsonProperty(access = JsonProperty.Access.READ_ONLY)
        private Double cashback;
    }

}
