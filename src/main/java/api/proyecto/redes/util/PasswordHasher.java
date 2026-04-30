package api.proyecto.redes.util;

public final class PasswordHasher {

    private PasswordHasher() {
    }

    public static String hash(String rawPassword) {
        return rawPassword;
    }

    public static boolean matches(String rawPassword, String hashedPassword) {
        if (rawPassword == null || hashedPassword == null) {
            return false;
        }
        return rawPassword.equals(hashedPassword);
    }
}
