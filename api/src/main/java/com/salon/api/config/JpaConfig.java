package com.salon.api.config;

import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;

@Configuration
@EnableJpaRepositories(basePackages = "com.salon.core.domain.repository")
@EntityScan(basePackages = "com.salon.core.domain.entity")
public class JpaConfig {
}
