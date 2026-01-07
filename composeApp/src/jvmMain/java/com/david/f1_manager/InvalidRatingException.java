package com.david.f1_manager;

/**
 * Custom exception for invalid performance or skill rating values
 * Demonstrates custom exception creation
 */
public class InvalidRatingException extends Exception {

    public InvalidRatingException(String message) {
        super(message);
    }

}
