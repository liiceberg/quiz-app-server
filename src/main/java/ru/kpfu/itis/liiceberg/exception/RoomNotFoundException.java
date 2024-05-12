package ru.kpfu.itis.liiceberg.exception;

public class RoomNotFoundException extends Exception {
    public RoomNotFoundException() {
    }

    public RoomNotFoundException(String message) {
        super(message);
    }
}
