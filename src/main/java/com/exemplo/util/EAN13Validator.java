package com.exemplo.util;

public class EAN13Validator {

    public static boolean isValidEAN13(String ean13) {
        if (ean13 == null || ean13.length() != 13 || !ean13.matches("\\d+")) {
            return false;
        }

        int sum = 0;
        for (int i = 0; i < 12; i++) {
            int digit = Character.getNumericValue(ean13.charAt(i));
            sum += (i % 2 == 0) ? digit : digit * 3;
        }

        int checkDigit = (10 - (sum % 10)) % 10;
        return checkDigit == Character.getNumericValue(ean13.charAt(12));
    }
}
