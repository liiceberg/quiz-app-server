package ru.kpfu.itis.liiceberg.util;

public enum ResponseCode {
    SUCCESS(0),
    FAILURE(1),
    NO_RESULTS(2),
    INVALID_PARAMETER(3);

    private final int code;

    ResponseCode(int code) {
        this.code = code;
    }

    public int getCode() {
        return code;
    }
}
