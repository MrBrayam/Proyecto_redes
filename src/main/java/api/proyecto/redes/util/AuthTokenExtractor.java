package api.proyecto.redes.util;

public final class AuthTokenExtractor {

    private AuthTokenExtractor() {
    }

    public static String extraerToken(String authorization, String tokenHeader) {
        if (tokenHeader != null && !tokenHeader.isBlank()) {
            return tokenHeader.trim();
        }
        if (authorization == null || authorization.isBlank()) {
            return null;
        }
        String normalized = authorization.trim();
        if (normalized.toLowerCase().startsWith("bearer ")) {
            return normalized.substring(7).trim();
        }
        return normalized;
    }
}
