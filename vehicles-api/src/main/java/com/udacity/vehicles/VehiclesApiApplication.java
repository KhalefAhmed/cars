package com.udacity.vehicles;

import com.udacity.vehicles.eureka.EurekaEndpoint;
import com.udacity.vehicles.domain.manufacturer.Manufacturer;
import com.udacity.vehicles.domain.manufacturer.ManufacturerRepository;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.DiscoveryClient;
import org.springframework.cloud.netflix.eureka.EnableEurekaClient;
import org.springframework.context.annotation.Bean;
import org.springframework.core.env.Environment;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;
import org.springframework.web.reactive.function.client.WebClient;

/**
 * Launches a Spring Boot application for the Vehicles API,
 * initializes the car manufacturers in the database,
 * and launches web clients to communicate with maps and pricing.
 */
@SpringBootApplication
@EnableJpaAuditing
@EnableEurekaClient
public class VehiclesApiApplication {

    public static void main(String[] args) {
        SpringApplication.run(VehiclesApiApplication.class, args);
    }

    @Autowired
    Environment env;

    @Autowired
    private DiscoveryClient discoveryClient;

    /**
     * Initializes the car manufacturers available to the Vehicle API.
     * @param repository where the manufacturer information persists.
     * @return the car manufacturers to add to the related repository
     */
    @Bean
    CommandLineRunner initDatabase(ManufacturerRepository repository) {
        return args -> {
            repository.save(new Manufacturer(100, "Audi"));
            repository.save(new Manufacturer(101, "Chevrolet"));
            repository.save(new Manufacturer(102, "Ford"));
            repository.save(new Manufacturer(103, "BMW"));
            repository.save(new Manufacturer(104, "Dodge"));
        };
    }

    @Bean
    public ModelMapper modelMapper() {
        return new ModelMapper();
    }

    /**
     * Web Client for the maps (location) API
     * @param endpoint where to communicate for the maps API
     * @return created maps endpoint
     */
    @Bean(name="maps")
    public WebClient webClientMaps(@Value("${maps.endpoint}") String endpoint) {
        return WebClient.create(endpoint);
    }

    /**
     * Web Client for the pricing API
     * @param endpoint where to communicate for the pricing API
     * @return created pricing endpoint
     */
    @Bean(name="pricing")
    public WebClient webClientPricing(@Value("${pricing.endpoint}") String endpoint) {
        return WebClient.create(endpoint);
    }

    /**
     * DONE! : Find the service dynamically from the eureka server.
     *
     * This method provides the endpoint url of the Pricing API
     * on the Eureka server.  It returns an endpoint for a local server
     * instance if a remote connection via Eureka is not available.
     *
     *  @param connectToEureka if true discover and use remote service API,
     *                        if false use local endpoint.
     *  @param serviceName the name of the Eureka service to discover.
     *  @param localEndpoint where to communicate for the pricing API.
     *  @return created pricing endpoint url
     */
    @Bean(name="eurekaUrl")
    public String eurekaUrl (
            @Value("${pricing.endpoint.use.eureka:false}") boolean connectToEureka,
            @Value("${pricing.service.name:PRICING-SERVICE}") String serviceName,
            @Value("${pricing.endpoint.local:http://localhost:8082}") String localEndpoint) {

        EurekaEndpoint endpoint = new EurekaEndpoint(discoveryClient, connectToEureka, serviceName, localEndpoint);
        return endpoint.lookup();
    }

    @Bean(name="vehicleServerUrl")
    public String vehicleServerUrl (@Value("${server.hostname:localhost}") String host,
                                    @Value("${server.port:8080}") String port) {
        return "http://" + host + ":" + port;
    }

}
