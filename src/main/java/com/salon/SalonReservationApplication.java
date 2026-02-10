package com.salon;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.elasticsearch.ElasticsearchClientAutoConfiguration;
import org.springframework.boot.autoconfigure.elasticsearch.ElasticsearchRestClientAutoConfiguration;
import org.springframework.boot.autoconfigure.data.elasticsearch.ElasticsearchDataAutoConfiguration;

@SpringBootApplication(exclude = {
    ElasticsearchClientAutoConfiguration.class,
    ElasticsearchRestClientAutoConfiguration.class,
    ElasticsearchDataAutoConfiguration.class
})
public class SalonReservationApplication {

    public static void main(String[] args) {
        SpringApplication.run(SalonReservationApplication.class, args);
    }
}
