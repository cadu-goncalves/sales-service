package com.viniland.sales.domain.rest.filter;

import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.annotations.ApiModel;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

import javax.validation.constraints.Max;
import javax.validation.constraints.PastOrPresent;
import javax.validation.constraints.Positive;
import javax.validation.constraints.PositiveOrZero;
import java.util.Date;

@ApiModel
@Data
@NoArgsConstructor
@ToString
public class SaleFilter {

    @PositiveOrZero(message = "{filter.page.invalid}")
    private Integer page = 0;

    @Positive(message = "{filter.size.invalid}")
    @Max(value = 50, message = "{filter.size.invalid}")
    private Integer size = 10;

    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "dd/MM/yyyy")
    @PastOrPresent(message = "{filter.date.from.invalid}")
    private Date from;

    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "dd/MM/yyyy")
    private Date to;
}
