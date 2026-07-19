package com.minimarket.security;

import com.minimarket.security.util.JwtUtil;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.test.util.ReflectionTestUtils;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

class JwtUtilTest {

    private JwtUtil jwtUtil;
    private UserDetails userDetails;

    @BeforeEach
    void setUp() {
        jwtUtil = new JwtUtil();
        ReflectionTestUtils.setField(jwtUtil, "secret",
                "bWluaW1hcmtldC1wbHVzLWNsYXZlLWRlLWRlc2Fycm9sbG8tMjAyNg==");
        ReflectionTestUtils.setField(jwtUtil, "expirationMs", 3_600_000L);
        userDetails = User.withUsername("admin")
                .password("password")
                .roles("ADMIN")
                .build();
    }

    @Test
    void generaTokenParaElUsuarioAutenticado() {
        String token = jwtUtil.generateToken(userDetails);

        assertEquals("admin", jwtUtil.extractUsername(token));
        assertTrue(jwtUtil.isTokenValid(token, userDetails));
    }
}
