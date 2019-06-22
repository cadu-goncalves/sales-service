package com.viniland.sales.domain.rest.filter;

import io.swagger.annotations.ApiModel;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

import javax.validation.constraints.Max;
import javax.validation.constraints.Positive;
import javax.validation.constraints.PositiveOrZero;

@ApiModel
@Data
@NoArgsConstructor
@ToString
public class AlbumFilter {

    @PositiveOrZero(message = "{filter.page.invalid}")
    private Integer page = 0;

    @Positive(message = "{filter.size.invalid}")
    @Max(value = 50, message = "{filter.size.invalid}")
    private Integer size = 10;

    private String genre;

}
