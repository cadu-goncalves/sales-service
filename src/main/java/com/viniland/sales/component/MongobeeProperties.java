package com.viniland.sales.component;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

/**
 * Mongobee configurations mapping
 */
@Component
@Data
@ConfigurationProperties("mongobee")
public class MongobeeProperties {

    private String packageScan;
}
