package com.ibs.vi.config;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@Configuration
@ConfigurationProperties(prefix = "amadeus")
public class AmadeusConfig {

    private String clientId;
    private String clientSecret;
    private String tokenUrl;
    private String flightOffersUrl;

    // Getters and setters
    public String getClientId() {
        return clientId;
    }
    public void setClientId(String clientId) {
        this.clientId = clientId;
    }
    public String getClientSecret() {
        return clientSecret;
    }
    public void setClientSecret(String clientSecret) {
        this.clientSecret = clientSecret;
    }
    public String getTokenUrl() {
        return tokenUrl;
    }
    public void setTokenUrl(String tokenUrl) {
        this.tokenUrl = tokenUrl;
    }
    public String getFlightOffersUrl() {
        return flightOffersUrl;
    }
    public void setFlightOffersUrl(String flightOffersUrl) {
        this.flightOffersUrl = flightOffersUrl;
    }
}