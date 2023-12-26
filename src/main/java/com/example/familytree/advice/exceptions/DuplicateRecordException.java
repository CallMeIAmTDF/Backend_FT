package com.example.familytree.advice.exceptions;

public class DuplicateRecordException extends RuntimeException {
    public DuplicateRecordException(String message) {
        super(message);
    }
}
