package com.viniland.sales.domain.rest;

import io.swagger.annotations.ApiModel;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

@ApiModel
@Data
@NoArgsConstructor
@EqualsAndHashCode(of = "id")
public class SaleResource {

    private String id;

}
