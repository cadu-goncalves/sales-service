package com.viniland.sales.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.mongodb.MongoDbFactory;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.convert.DefaultDbRefResolver;
import org.springframework.data.mongodb.core.convert.DefaultMongoTypeMapper;
import org.springframework.data.mongodb.core.convert.MappingMongoConverter;
import org.springframework.data.mongodb.core.convert.MongoCustomConversions;
import org.springframework.data.mongodb.core.mapping.MongoMappingContext;

/**
 * Bootstrap MongoDB integration.
 */
@Configuration
public class MongoDbConfig {

    /**
     * Customize {@link MongoTemplate}
     *
     * @param factory {@link MongoDbFactory}
     * @param conversions {@link MongoCustomConversions}
     * @return {@link MongoTemplate}
     */
    @Bean
    public MongoTemplate mongoTemplate(MongoDbFactory factory, MongoCustomConversions conversions) {
        // Don't include "_class" attribute on collections
        DefaultDbRefResolver resolver = new DefaultDbRefResolver(factory);
        MappingMongoConverter converter = new MappingMongoConverter(resolver, new MongoMappingContext());
        converter.setTypeMapper(new DefaultMongoTypeMapper(null));
        converter.setCustomConversions(conversions);
        return new MongoTemplate(factory, converter);
    }

}