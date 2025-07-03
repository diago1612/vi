package com.ibs.vi.service;

import com.ibs.vi.config.AmadeusConfig;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

@Service
public class AmadeusApiService {

    private static final Logger logger = LoggerFactory.getLogger(AmadeusApiService.class);

    private final RestTemplate restTemplate;
    private final AmadeusAuthService authService;
    private final AmadeusConfig config;

    public AmadeusApiService(RestTemplate restTemplate, AmadeusAuthService authService, AmadeusConfig config) {
        this.restTemplate = restTemplate;
        this.authService = authService;
        this.config = config;
    }

    public String getFlightOffers() {
        String token = authService.getAccessToken();
        if (token == null) {
            return null;
        }

        HttpHeaders headers = new HttpHeaders();
        headers.setBearerAuth(token);
        headers.setAccept(java.util.List.of(MediaType.APPLICATION_JSON));

        HttpEntity<String> entity = new HttpEntity<>(headers);

        try {
            ResponseEntity<String> response = restTemplate.exchange(
                    config.getFlightOffersUrl(), HttpMethod.GET, entity, String.class);
            if (response.getStatusCode() == HttpStatus.OK) {
                return response.getBody();
            } else {
                logger.error("Failed to get flight offers: {}", response.getStatusCode());
                return null;
            }
        } catch (Exception ex) {
            logger.error("Error calling flight offers API", ex);
            return null;
        }
    }
}
