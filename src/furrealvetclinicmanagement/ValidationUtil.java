package furrealvetclinicmanagement;

public final class ValidationUtil {
    private ValidationUtil() {
    }

    public static String clean(String value, String placeholder) {
        String cleaned = value == null ? "" : value.trim();
        return cleaned.equalsIgnoreCase(placeholder) ? "" : cleaned;
    }

    public static boolean hasBlank(String... values) {
        for (String value : values) {
            if (value == null || value.trim().isEmpty()) {
                return true;
            }
        }
        return false;
    }

    public static boolean isValidEmail(String email) {
        return email != null && email.trim().matches("(?i)^[a-z0-9._%+-]+@gmail\\.com$");
    }

    public static Integer parseAge(String value) {
        try {
            int age = Integer.parseInt(value == null ? "" : value.trim());
            return age >= 0 ? age : null;
        } catch (NumberFormatException ex) {
            return null;
        }
    }
}
