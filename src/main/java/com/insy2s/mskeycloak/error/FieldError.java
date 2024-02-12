package com.insy2s.mskeycloak.error;

public record FieldError(
        String entityName,
        String fieldName,
        String message,
        String code
) {
}
