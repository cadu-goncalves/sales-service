package com.viniland.sales.config;

import com.github.mongobee.Mongobee;
import com.mongodb.MongoClient;
import com.viniland.sales.component.MongobeeProperties;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.mongo.MongoProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.env.Environment;

/**
 * Bootstrap MongoDB integration.
 */
@Configuration
public class MongobeeConfig {

    @Autowired
    private MongoProperties mongoProperties;

    @Autowired
    private MongobeeProperties mongobeeProperties;

    /**
     * Factory for database migration runner
     *
     * @param environment {@link Environment}
     * @param  client {@link MongoClient}
     * @return {@link Mongobee}
     */
    @Bean
    public Mongobee mongobee(Environment environment, MongoClient client) {
        Mongobee runner = new Mongobee(client);
        runner.setDbName(mongoProperties.getDatabase());
        // Define where to look for changelogs
        runner.setChangeLogsScanPackage(mongobeeProperties.getPackageScan());
        // Pass environment in order to track profiles
        runner.setSpringEnvironment(environment);
        return runner;
    }
}
