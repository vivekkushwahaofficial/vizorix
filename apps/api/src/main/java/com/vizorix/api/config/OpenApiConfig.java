package com.vizorix.api.config;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * Configuration class to set up SpringDoc OpenAPI UI parameters. Serves API specification schemas
 * via http://localhost:8080/swagger-ui/index.html
 */
@Configuration
public class OpenApiConfig {

  private final ApiProperties apiProperties;

  public OpenApiConfig(ApiProperties apiProperties) {
    this.apiProperties = apiProperties;
  }

  /**
   * Defines the custom OpenAPI documentation bean.
   *
   * @return standard OpenAPI metadata descriptor
   */
  @Bean
  public OpenAPI customOpenApi() {
    return new OpenAPI()
        .info(
            new Info()
                .title(apiProperties.getAppTitle())
                .description(apiProperties.getAppDescription())
                .version(apiProperties.getVersion()));
  }
}
