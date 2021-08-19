package com.revature.johnKimAPI.util.exceptions;

/**
 * Occurs whenever there is an issue persisting data to MongoDB via the methods coded in SchoolRepository.
 */

public class ResourcePersistenceException extends RuntimeException {
    public ResourcePersistenceException(String message) {
        super(message);
    }
}
