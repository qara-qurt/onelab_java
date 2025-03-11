package org.onelab.restaurant_service.exception;


public class AlreadyExistException extends RuntimeException{
    public AlreadyExistException(String message) {
        super(message);
    }
}

