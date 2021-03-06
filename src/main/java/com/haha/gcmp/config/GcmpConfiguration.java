package com.haha.gcmp.config;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.haha.gcmp.cache.AbstractStringCacheStore;
import com.haha.gcmp.cache.InMemoryCacheStore;
import com.haha.gcmp.config.propertites.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.converter.json.Jackson2ObjectMapperBuilder;

/**
 * Gcmp configuration.
 *
 * @author SZFHH
 * @date 2020/10/23
 */
@Configuration
@EnableConfigurationProperties({GcmpProperties.class, FtpPoolConfig.class, SftpPoolConfig.class, FileSshPoolConfig.class, StatusSshPoolConfig.class})
public class GcmpConfiguration {
    private static final Logger log = LoggerFactory.getLogger(GcmpConfiguration.class);
    @Autowired
    GcmpProperties gcmpProperties;

    @Bean
    public ObjectMapper objectMapper(Jackson2ObjectMapperBuilder builder) {
        builder.failOnEmptyBeans(false);
        return builder.build();
    }

    @Bean
    @ConditionalOnMissingBean
    public AbstractStringCacheStore stringCacheStore() {
        AbstractStringCacheStore stringCacheStore;
        switch (gcmpProperties.getCache()) {
            case "memory":
            default:
                //memory or default
                stringCacheStore = new InMemoryCacheStore();
                break;

        }
        return stringCacheStore;

    }
}
