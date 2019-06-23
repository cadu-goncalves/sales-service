package com.viniland.sales.domain.event;

import lombok.Value;

import javax.validation.constraints.NotNull;
import javax.validation.constraints.Positive;

/**
 * Cashback credit event
 */
@Value
public class CashbackCreditEvent {

    @NotNull
    private Long customerId;

    @Positive
    private Double amount;
}
