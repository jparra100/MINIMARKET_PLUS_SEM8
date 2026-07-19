package com.minimarket.security.model;

import java.util.List;

public record AuthResponse(String token, String type, String username, List<String> roles) {
}
