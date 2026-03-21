package com.smartintern.security;

import com.smartintern.enums.Role;
import com.smartintern.model.User;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.lang.reflect.Field;

import static org.assertj.core.api.Assertions.assertThat;

class JwtTokenProviderTest {

    private JwtTokenProvider jwtTokenProvider;

    @BeforeEach
    void setUp() throws Exception {
        jwtTokenProvider = new JwtTokenProvider();
        setField(jwtTokenProvider, "jwtSecret", "VGVzdFNlY3JldEtleUZvckpXVFRva2VuR2VuZXJhdGlvbjI1NkJpdHNNaW5pbXVtTGVuZ3Ro");
        setField(jwtTokenProvider, "jwtExpiration", 86400000L);
    }

    @Test
    void generateToken_containsExpectedClaims() {
        User user = User.builder()
                .id(1L)
                .email("test@example.com")
                .role(Role.ETUDIANT)
                .build();

        String token = jwtTokenProvider.generateToken(user);

        assertThat(token).isNotNull().isNotEmpty();
        assertThat(jwtTokenProvider.getEmailFromToken(token)).isEqualTo("test@example.com");
        assertThat(jwtTokenProvider.getUserIdFromToken(token)).isEqualTo(1L);
        assertThat(jwtTokenProvider.getRoleFromToken(token)).isEqualTo("ETUDIANT");
    }

    @Test
    void validateToken_validToken_returnsTrue() {
        User user = User.builder()
                .id(1L)
                .email("test@example.com")
                .role(Role.ETUDIANT)
                .build();

        String token = jwtTokenProvider.generateToken(user);

        assertThat(jwtTokenProvider.validateToken(token)).isTrue();
    }

    @Test
    void validateToken_invalidToken_returnsFalse() {
        assertThat(jwtTokenProvider.validateToken("invalid.token.here")).isFalse();
    }

    @Test
    void validateToken_tamperedToken_returnsFalse() {
        User user = User.builder()
                .id(1L)
                .email("test@example.com")
                .role(Role.ETUDIANT)
                .build();

        String token = jwtTokenProvider.generateToken(user);
        String tampered = token.substring(0, token.length() - 5) + "xxxxx";

        assertThat(jwtTokenProvider.validateToken(tampered)).isFalse();
    }

    @Test
    void getUserIdFromToken_returnsCorrectId() {
        User user = User.builder()
                .id(42L)
                .email("user@test.com")
                .role(Role.ENTREPRISE)
                .build();

        String token = jwtTokenProvider.generateToken(user);

        assertThat(jwtTokenProvider.getUserIdFromToken(token)).isEqualTo(42L);
    }

    @Test
    void getRoleFromToken_returnsCorrectRole() {
        User user = User.builder()
                .id(1L)
                .email("admin@test.com")
                .role(Role.ADMIN)
                .build();

        String token = jwtTokenProvider.generateToken(user);

        assertThat(jwtTokenProvider.getRoleFromToken(token)).isEqualTo("ADMIN");
    }

    private void setField(Object target, String fieldName, Object value) throws Exception {
        Field field = target.getClass().getDeclaredField(fieldName);
        field.setAccessible(true);
        field.set(target, value);
    }
}
