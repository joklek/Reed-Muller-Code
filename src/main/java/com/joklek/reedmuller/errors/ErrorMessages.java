package com.joklek.reedmuller.errors;

public class ErrorMessages {
    private ErrorMessages() {
    }

    public static String ERROR_RATE_NOT_DECIMAL(String actual) {
        return String.format("\"-e\" should be decimal number between 0.0 and 0.100, but is %s", actual);
    }
    public static String M_NOT_INTEGER(String actual) {
        return String.format("Incorrect flags, \"-m\" should be integer, but is %s", actual);
    }
}
