package com.ecocycle.util;

import java.util.regex.Pattern;

public final class Validator {

    private static final Pattern EMAIL =
            Pattern.compile("^[A-Za-z0-9._%+-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,}$");
    private static final Pattern PHONE = Pattern.compile("^[0-9]{10}$");

    private Validator() { }

    public static boolean isBlank(String s) {
        return s == null || s.trim().isEmpty();
    }

    public static boolean isValidEmail(String s) {
        return s != null && EMAIL.matcher(s.trim()).matches();
    }

    /** Exactly 10 digits. Loosen this pattern if you want to allow country codes. */
    public static boolean isValidPhone(String s) {
        return s != null && PHONE.matcher(s.trim()).matches();
    }

    /** 8 to 72 characters with at least one letter and one digit (BCrypt only reads 72). */
    public static boolean isStrongPassword(String s) {
        if (s == null || s.length() < 8 || s.length() > 72) {
            return false;
        }
        boolean hasLetter = false;
        boolean hasDigit = false;
        for (char c : s.toCharArray()) {
            if (Character.isLetter(c)) {
                hasLetter = true;
            }
            if (Character.isDigit(c)) {
                hasDigit = true;
            }
        }
        return hasLetter && hasDigit;
    }

    public static boolean tooLong(String s, int maxLength) {
        return s != null && s.length() > maxLength;
    }
}