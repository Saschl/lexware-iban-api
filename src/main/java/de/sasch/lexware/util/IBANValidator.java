package de.sasch.lexware.util;

import java.math.BigInteger;

public class IBANValidator {

    public static boolean isIbanValid(String iban) {
        if (iban == null || iban.length() < 15 || iban.length() > 34) {
            return false;
        }

        // move the first four characters to the end of the string
        String reformattedIban = iban.substring(4) + iban.substring(0, 4);
        StringBuilder numericIban = new StringBuilder();

        // transform the characters to numbers
        for (char character : reformattedIban.toCharArray()) {
            int numericValue = Character.getNumericValue(character);
            // numbers 0-9 are valid, letters A-Z are valid (10-35)
            if (numericValue < 0 || numericValue > 35) {
                return false;
            }
            numericIban.append(numericValue);
        }

        BigInteger ibanNumber = new BigInteger(numericIban.toString());
        return ibanNumber.mod(BigInteger.valueOf(97)).intValue() == 1;
    }
}
