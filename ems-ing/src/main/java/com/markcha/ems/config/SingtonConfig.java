package com.markcha.ems.config;

import com.fasterxml.jackson.core.json.JsonReadFeature;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.MapperFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.querydsl.jpa.impl.JPAQueryFactory;
import org.modelmapper.ModelMapper;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.converter.HttpMessageConverter;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;
import org.springframework.web.client.RestTemplate;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import java.util.ArrayList;
import java.util.List;

@Configuration
public class SingtonConfig {
    @PersistenceContext
    private EntityManager entityManager;
    @Bean
    public ModelMapper modelMapper() {
        return new ModelMapper();
    }
    @Bean
    public JPAQueryFactory jpaQueryFactory() {
        return new JPAQueryFactory(entityManager);
    }

    @Bean
    public ObjectMapper objectMapper() {
        ObjectMapper mapper = new ObjectMapper();
        mapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
        mapper.configure(MapperFeature.DEFAULT_VIEW_INCLUSION, true);
        mapper.registerModule(new JavaTimeModule());

        return mapper;
    }
    @Bean
    public RestTemplate restTemplate() {
        RestTemplate restTemplate = new RestTemplate();
        ObjectMapper mapper = new ObjectMapper();
        mapper.enable(JsonReadFeature.ALLOW_NON_NUMERIC_NUMBERS.mappedFeature());
        mapper.configure(MapperFeature.DEFAULT_VIEW_INCLUSION, true);
        List<HttpMessageConverter<?>> messageConverters = new ArrayList<>();
        MappingJackson2HttpMessageConverter jsonMessageConverter = new MappingJackson2HttpMessageConverter();
        jsonMessageConverter.setObjectMapper(mapper);
        messageConverters.add(jsonMessageConverter);
        restTemplate.setMessageConverters(messageConverters);
        return restTemplate;

    }

}