package com.udacity.vehicles.config;

import io.swagger.annotations.Api;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import springfox.documentation.builders.PathSelectors;
import springfox.documentation.builders.RequestHandlerSelectors;
import springfox.documentation.service.ApiInfo;
import springfox.documentation.service.Contact;
import springfox.documentation.spi.DocumentationType;
import springfox.documentation.spring.web.plugins.Docket;
import springfox.documentation.swagger2.annotations.EnableSwagger2;

import java.util.Collections;

@Configuration
@EnableSwagger2
public class SwaggerConfig {

    private String apiTitle = "Vehicle Service API";
    private String apiDescription = "A vehicle listing service of cars that may or may not exist with bogus prices, in places you'll never find.";
    private String apiVersion = "0.1";
    private Contact apiContact = new springfox.documentation.service.Contact("Ahmed KHALEF", "https://github.com/KhalefAhmed", "khalef.ahmedkhalil@gmail.com");


    @Bean
    public Docket api(@Qualifier("vehicleServerUrl") String url) {
        return new Docket(DocumentationType.SWAGGER_2)
                .select()
                .apis(RequestHandlerSelectors.withClassAnnotation(Api.class))
                .paths(PathSelectors.any())
                .build()
                .apiInfo(getApiInfo(url))
                .useDefaultResponseMessages(false);
    }

    private ApiInfo getApiInfo(String url) {

        ApiInfo info = new ApiInfo(
                apiTitle,
                apiDescription,
                apiVersion,
                "https://www.univ-orleans.fr/fr",
                apiContact,
                "License of Cars API", "https://www.univ-orleans.fr/fr", Collections.emptyList());
        return info;
    }

}
