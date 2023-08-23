package com.example.demo.service;

import java.io.IOException;
import java.util.Arrays;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.HttpServerErrorException;
import org.springframework.web.client.RestTemplate;

import com.example.demo.model.Customer;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

@Service
public class CustomerService {
    
	
    private final RestTemplate restTemplate;
    private final String baseUrl;

    @Autowired
    public CustomerService(RestTemplate restTemplate, @Value("${api.base-url}") String baseUrl) {
        this.restTemplate = restTemplate;
        this.baseUrl = baseUrl;
    }

    public String authenticate(String loginId, String password) {
        String url = baseUrl + "/assignment_auth.jsp";

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);

        //AuthRequest request = new AuthRequest(loginId, password);
        String requestBody = "{\"login_id\": \"" + loginId + "\", \"password\": \"" + password + "\"}";
        HttpEntity<String> entity = new HttpEntity<>(requestBody, headers);
        
        //ResponseEntity<String> response = restTemplate.postForEntity(url, entity, String.class);
        try {
            ResponseEntity<String> response = restTemplate.exchange(url, HttpMethod.POST, entity, String.class);

            if (response.getStatusCode() == HttpStatus.OK) {
                // Authentication successful, return the token
            	//System.out.println(extractTokenValue(response.getBody()));
                return extractTokenValue(response.getBody());
            } else {
                // Handle other responses as needed
                throw new HttpClientErrorException(response.getStatusCode());
            }
        } catch (HttpClientErrorException ex) {
            // Wrap the exception to add more context
            throw new RuntimeException("API request error: Authentication failed", ex);
        }
    }

    private String extractTokenValue(String responseBody) {
        try {
            ObjectMapper objectMapper = new ObjectMapper();
            JsonNode rootNode = objectMapper.readTree(responseBody);
            return rootNode.get("access_token").asText();
        } catch (IOException e) {
            e.printStackTrace();
            return "Error extracting token";
        }
    }
    
    

    public List<Customer> getCustomerList(String token) {
        String url = baseUrl + "/assignment.jsp?cmd=get_customer_list";

        HttpHeaders headers = new HttpHeaders();
        headers.set(HttpHeaders.AUTHORIZATION, "Bearer " + token);
        HttpEntity<String> entity = new HttpEntity<>(headers);

        ResponseEntity<Customer[]> response = restTemplate.exchange(
            url, HttpMethod.GET, entity, Customer[].class);

        return Arrays.asList(response.getBody());
    }
    
    public ResponseEntity<String> createCustomer(String token, Customer customer) {
        String createCustomerURL = baseUrl + "/assignment.jsp?cmd=create";

        HttpHeaders headers = new HttpHeaders();
        headers.set(HttpHeaders.AUTHORIZATION, "Bearer " + token);
        
        String requestBody = "{\"first_name\": \"" + customer.getFirst_name() + "\", \"last_name\": \"" + customer.getLast_name() + "\", \"street\": \"" + customer.getStreet() + "\", \"address\": \"" + customer.getAddress() + "\", \"city\": \"" + customer.getCity() + "\", \"state\": \"" + customer.getState() + "\", \"email\": \"" + customer.getEmail() + "\", \"phone\": \"" + customer.getPhone() + "\"}";
        System.out.println(customer.getFirst_name()+" "+token);
        HttpEntity<String> entity = new HttpEntity<>(requestBody, headers);

        try {
            ResponseEntity<String> response = restTemplate.exchange(
                createCustomerURL, HttpMethod.POST, entity, String.class);
     System.out.println(response);
            return response;
        } catch (HttpServerErrorException | HttpClientErrorException e) {
        	System.out.println(ResponseEntity.status(e.getRawStatusCode())
                                .body(e.getResponseBodyAsString()));
            return ResponseEntity.status(e.getRawStatusCode())
                                .body(e.getResponseBodyAsString());
        }
    }
       
    
    public ResponseEntity<String> deleteCustomer(String token, String uuid) {
        String deleteCustomerURL = baseUrl + "/assignment.jsp?cmd=delete&uuid=" + uuid;

        HttpHeaders headers = new HttpHeaders();
        headers.set("Authorization", "Bearer " + token);

        HttpEntity<String> entity = new HttpEntity<>(headers);

        try {
            ResponseEntity<String> response = restTemplate.exchange(
                deleteCustomerURL, HttpMethod.POST, entity, String.class);
            return response;
        } catch (HttpServerErrorException | HttpClientErrorException e) {
            return ResponseEntity.status(e.getRawStatusCode())
                                .body(e.getResponseBodyAsString());
        }
    }
}
