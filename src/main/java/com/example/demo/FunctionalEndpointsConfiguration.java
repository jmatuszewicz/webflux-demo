package com.example.demo;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.reactive.function.server.RouterFunction;
import org.springframework.web.reactive.function.server.RouterFunctions;

import static org.springframework.web.reactive.function.server.RequestPredicates.GET;

@Configuration
public class FunctionalEndpointsConfiguration {

    @Autowired
    private CityRepository repository;

    @Bean
    public RouterFunction<?> routerFunction(CitiesHandler citiesHandler) {
        return RouterFunctions
                .route(GET("/fn/cities/{id}"), citiesHandler::getCity)
                .andRoute(GET("/fn/cities"), citiesHandler::getCities)
                .andRoute(GET("/fn/cities/stream"), citiesHandler::getCitiesStream);
    }

}
