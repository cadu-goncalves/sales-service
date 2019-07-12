package com.viniland.sales.util.rest;

import com.viniland.sales.domain.rest.page.ResourcePage;
import lombok.experimental.UtilityClass;
import org.modelmapper.ModelMapper;
import org.springframework.data.domain.Page;

import java.util.List;
import java.util.stream.Collectors;

/**
 * Resource utils
 */
@UtilityClass
public class ResourceBuilder {

    private static final ModelMapper MAPPER;

    static {
        MAPPER = new ModelMapper();
    }

    /**
     * Create a resource.
     *
     * @param domain       domain object
     * @param resourceType resource class
     * @param <T>          resource class type param
     * @return resource of type T
     */
    public static <T> T asResource(final Object domain, Class<T> resourceType) {
        return MAPPER.map(domain, resourceType);
    }

    /**
     * Create a page of resources
     *
     * @param domain       domain page
     * @param resourceType resource class
     * @param <T>          resource class type param
     * @return {@link ResourcePage}
     */
    public static <T> ResourcePage<T> asPage(Page<?> domain, Class<T> resourceType) {
        // Map resources
        List<T> resources = domain.getContent().stream()
                .map(v -> asResource(v, resourceType))
                .collect(Collectors.toList());

        // Build page
        return ResourcePage.<T>builder()
                .content(resources)
                .page(domain.getNumber())
                .totalPages(domain.getTotalPages())
                .matches(domain.getTotalElements())
                .build();
    }
}
