package com.resturant.mskeycloak.error;

public record FieldError(
        String entityName,
        String fieldName,
        String message,
        String code
) {
}
