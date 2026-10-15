package com.urbanassist;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.data.web.config.EnableSpringDataWebSupport;

/**
 * UrbanAssistApplication — The main entry point for the Spring Boot application.
 *
 * What @SpringBootApplication does (3 things in one annotation):
 *   1. @Configuration    — Marks this class as a source of bean definitions.
 *   2. @EnableAutoConfiguration — Tells Spring Boot to automatically configure
 *                                 beans based on the dependencies in pom.xml
 *                                 (e.g., sets up DataSource if MySQL driver is found).
 *   3. @ComponentScan    — Scans the com.urbanassist package (and all sub-packages)
 *                          for classes annotated with @Controller, @Service,
 *                          @Repository, @Component, etc.
 *
 * When you run this class, Spring Boot:
 *   - Starts an embedded Tomcat server on port 8080 (default).
 *   - Registers all your REST controllers.
 *   - Connects to the database (when configured).
 *   - Makes your API available at http://localhost:8080.
 *
 * @EnableSpringDataWebSupport(pageSerializationMode = VIA_DTO)
 *   Produces stable, predictable JSON for paginated responses (Page&lt;T&gt;).
 *   Without this, Spring Data logs a WARN on every paginated endpoint.
 */
@SpringBootApplication
@EnableSpringDataWebSupport(pageSerializationMode = EnableSpringDataWebSupport.PageSerializationMode.VIA_DTO)
public class UrbanAssistApplication {

    public static void main(String[] args) {
        SpringApplication.run(UrbanAssistApplication.class, args);
    }
}
