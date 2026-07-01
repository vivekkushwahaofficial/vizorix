package com.vizorix.api.config;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

/**
 * Type-safe configuration properties for the Vizorix API. Maps keys prefixed with 'vizorix.api'
 * from application configuration files.
 */
@Configuration
@ConfigurationProperties(prefix = "vizorix.api")
@Getter
@Setter
public class ApiProperties {
  private String appTitle;
  private String appDescription;
  private String version;
}
