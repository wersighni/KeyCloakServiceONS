package com.insy2s.keycloakauth.error;

public record FieldError(
        String entityName,
        String fieldName,
        String message,
        String code
) {
}
