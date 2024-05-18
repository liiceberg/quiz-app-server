package ru.kpfu.itis.liiceberg.exception;

import lombok.NoArgsConstructor;

@NoArgsConstructor
public class BadArgumentsException extends Exception {

    public BadArgumentsException(String message) {
        super(message);
    }
}
