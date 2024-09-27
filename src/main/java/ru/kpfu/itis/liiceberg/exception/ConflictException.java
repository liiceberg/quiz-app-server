package ru.kpfu.itis.liiceberg.exception;


import lombok.NoArgsConstructor;

@NoArgsConstructor
public class ConflictException extends Exception {

    public ConflictException(String message) {
        super(message);
    }
}