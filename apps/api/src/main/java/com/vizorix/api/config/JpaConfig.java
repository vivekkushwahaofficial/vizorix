package com.vizorix.api.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;

/**
 * Configuration class enabling JPA Auditing. Separated from the main ApiApplication to allow slice
 * testing (@WebMvcTest) to boot without requiring JPA dependencies.
 */
@Configuration
@EnableJpaAuditing
public class JpaConfig {}
