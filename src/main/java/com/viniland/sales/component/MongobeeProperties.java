package com.viniland.sales.component;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

import java.util.Map;

/**
 * Mongobee configurations mapping
 */
@Component
@Data
@ConfigurationProperties("mongobee")
public class MongobeeProperties {

    private Boolean enabled;

    private String scan;

    private Map<String, String> collections;

}
