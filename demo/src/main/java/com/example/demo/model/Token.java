package com.example.demo.model;

import org.springframework.stereotype.Component;

@Component
public class Token {

    private String access_token;

    public String getToken() {
        return access_token;
    }

    public void setToken(String access_token) {
        this.access_token = access_token;
    }
}
