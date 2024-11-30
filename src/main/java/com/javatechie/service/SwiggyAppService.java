package com.javatechie.service;

import com.javatechie.client.RestaurantServiceClient;
import com.javatechie.dto.OrderResponseDTO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cloud.client.ServiceInstance;
import org.springframework.cloud.client.discovery.DiscoveryClient;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;

import java.io.IOException;
import java.util.Arrays;
import java.util.List;

@Service
public class SwiggyAppService {

    @Autowired
    private RestaurantServiceClient restaurantServiceClient;


    @Autowired
    private DiscoveryClient discoveryClient;

    public String greeting() {
        return "Welcome to Swiggy App Service 8089";
    }

    public OrderResponseDTO checkOrderStatus(String orderId) {
        return restaurantServiceClient.fetchOrderStatus(orderId);
    }

    public List<String> greetingHotels() {
        return Arrays.asList("HOtel Taz", "Hotel Bawarchi!", "Hotel alpha");
    }




    public ResponseEntity<List<String>> greetingHotelsDiscovery(String token) throws RestClientException, IOException {
        // Retrieve instances of the RESTAURANT-SERVICE
        List<ServiceInstance> instances = discoveryClient.getInstances("RESTAURANT-SERVICE");

        // Check if instances are available
        if (instances.isEmpty()) {
            throw new IllegalStateException("No instances of RESTAURANT-SERVICE found");
        }

        ServiceInstance serviceInstance = instances.get(0);
        String baseUrl = serviceInstance.getUri().toString() + "/restaurant/hotels";

        RestTemplate restTemplate = new RestTemplate();
        ResponseEntity<List<String>> response;

        try {
            // Create headers and add the token
            HttpHeaders headers = getHeaders(token);
            HttpEntity<Void> entity = new HttpEntity<>(headers);

            // Make the GET request and specify the expected response type
            response = restTemplate.exchange(baseUrl, HttpMethod.GET, entity, new ParameterizedTypeReference<List<String>>() {});
        } catch (RestClientException ex) {
            System.err.println("Error during REST call: " + ex.getMessage());
            throw ex; // Re-throw the exception after logging
        }

        // Return the response entity directly
        return response;
    }

    private HttpHeaders getHeaders(String token) {
        HttpHeaders headers = new HttpHeaders();
        headers.set("Authorization", "Bearer " + token); // Assuming Bearer token
        return headers;
    }
}

