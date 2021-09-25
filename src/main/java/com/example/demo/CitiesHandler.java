package com.example.demo;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.server.ServerRequest;
import org.springframework.web.reactive.function.server.ServerResponse;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@Component
public class CitiesHandler {

    @Autowired
    private CityRepository repository;

    public Mono<ServerResponse> getCity(ServerRequest request) {
        return ServerResponse.ok()
                .body(repository.findById(request.pathVariable("id")), City.class);
    }

    public Mono<ServerResponse> getCities(ServerRequest request) {
        Flux<City> cities = repository.findAll()
                .filter(city -> !"USA".equals(city.getCountry()));
        return ServerResponse.ok().body(cities, City.class);
    }

    public Mono<ServerResponse> getCitiesStream(ServerRequest request) {
        Flux<City> cities = repository.findAll()
                .filter(city -> !"USA".equals(city.getCountry()));
        return ServerResponse.ok().contentType(MediaType.APPLICATION_STREAM_JSON)
                .body(cities, City.class);
    }

}
