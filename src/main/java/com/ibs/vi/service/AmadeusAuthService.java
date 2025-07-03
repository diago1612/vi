package com.ibs.vi.service;

import com.ibs.vi.config.AmadeusConfig;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;

import java.util.Map;

@Service
public class AmadeusAuthService {

    private static final Logger logger = LoggerFactory.getLogger(AmadeusAuthService.class);

    private final RestTemplate restTemplate;
    private final AmadeusConfig config;

    public AmadeusAuthService(RestTemplate restTemplate, AmadeusConfig config) {
        this.restTemplate = restTemplate;
        this.config = config;
    }

    public String getAccessToken() {

        MultiValueMap<String, String> body = new LinkedMultiValueMap<>();
        body.add("grant_type", "client_credentials");
        body.add("client_id", config.getClientId());
        body.add("client_secret", config.getClientSecret());

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);
        headers.setAccept(java.util.List.of(MediaType.APPLICATION_JSON));

        HttpEntity<MultiValueMap<String, String>> request = new HttpEntity<>(body, headers);

        try {
            ResponseEntity<Map> response = restTemplate.postForEntity(config.getTokenUrl(), request, Map.class);
            if (response.getStatusCode() == HttpStatus.OK && response.getBody() != null) {
                return (String) response.getBody().get("access_token");
            } else {
                logger.error("Failed to retrieve access token: {}", response.getStatusCode());
                return null;
            }
        } catch (Exception ex) {
            logger.error("Error fetching access token", ex);
            return null;
        }
    }
}