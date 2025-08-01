package com.sistradoc.frontendvaadin;

// This class is used to deserialize the JWT from the backend
public class AuthResponse {
    private String jwt;

    public String getJwt() {
        return jwt;
    }

    public void setJwt(String jwt) {
        this.jwt = jwt;
    }
}
