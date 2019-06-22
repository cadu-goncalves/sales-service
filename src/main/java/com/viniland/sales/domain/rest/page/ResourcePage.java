package com.viniland.sales.domain.rest.page;

import io.swagger.annotations.ApiModel;
import lombok.*;

import java.util.List;

@ApiModel
@Value
@Builder
public class ResourcePage<T> {

    private Integer page;

    private Integer totalPages;

    private Long matches;

    private List<T> content;
}
