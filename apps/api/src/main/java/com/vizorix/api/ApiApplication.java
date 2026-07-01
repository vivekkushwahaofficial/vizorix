package com.vizorix.api;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;

/** Entry point class for launching the Vizorix API backend application. */
@SpringBootApplication
@EnableJpaAuditing
public class ApiApplication {

  /**
   * Main method mapping the JVM runtime start hook.
   *
   * @param args execution arguments
   */
  public static void main(String[] args) {
    SpringApplication.run(ApiApplication.class, args);
  }
}
