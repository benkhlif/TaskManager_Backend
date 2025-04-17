package com.ubci.fst.dto;

public class LoginResponse {
private String jwtToken;

public LoginResponse(String jwtToken) {
	super();
	this.jwtToken = jwtToken;
}

public String getJwtToken() {
	return jwtToken;
}

public void setJwtToken(String jwtToken) {
	this.jwtToken = jwtToken;
}
}