package com.haha.gcmp.config;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.haha.gcmp.cache.AbstractStringCacheStore;
import com.haha.gcmp.cache.InMemoryCacheStore;
import com.haha.gcmp.config.propertites.FtpPoolConfig;
import com.haha.gcmp.config.propertites.GcmpProperties;
import com.haha.gcmp.config.propertites.SftpPoolConfig;
import com.haha.gcmp.config.propertites.SshPoolConfig;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.converter.json.Jackson2ObjectMapperBuilder;

/**
 * @author SZFHH
 * @date 2020/10/23
 */
@Configuration
@EnableConfigurationProperties({GcmpProperties.class, FtpPoolConfig.class, SftpPoolConfig.class, SshPoolConfig.class})
public class GcmpConfiguration {
    private static final Logger log = LoggerFactory.getLogger(GcmpConfiguration.class);
    @Autowired
    GcmpProperties gcmpProperties;

    @Bean
    public ObjectMapper objectMapper(Jackson2ObjectMapperBuilder builder) {
        builder.failOnEmptyBeans(false);
        return builder.build();
    }

//    @Bean
//    public RestTemplate httpsRestTemplate(RestTemplateBuilder builder)
//        throws NoSuchAlgorithmException, KeyStoreException, KeyManagementException {
//        RestTemplate httpsRestTemplate = builder.build();
//        httpsRestTemplate.setRequestFactory(new HttpComponentsClientHttpRequestFactory(HttpClientUtils.createHttpsClient(
//            (int) haloProperties.getDownloadTimeout().toMillis())));
//        return httpsRestTemplate;
//    }

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
        log.info("halo cache store load impl : [{}]", stringCacheStore.getClass());
        return stringCacheStore;

    }
}
