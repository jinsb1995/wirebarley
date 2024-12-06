package com.wirebarley.infrastructure.common;

public final class StringUtils {
    private StringUtils() {
    }

    public static boolean isBlank(String str) {
        return str == null || str.isBlank();
    }
}
