package de.ellpeck.rockbottom.auth;

public class ManagementServerException extends RuntimeException {

    public ManagementServerException() {
    }

    public ManagementServerException(String message) {
        super(message);
    }

    public static final ManagementServerException NOT_YET_ARRIVED = new ManagementServerException("Attempted to access the response items before the response has arrived from the server");

}
