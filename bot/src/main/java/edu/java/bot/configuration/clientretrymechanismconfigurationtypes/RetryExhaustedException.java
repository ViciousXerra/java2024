package edu.java.bot.configuration.clientretrymechanismconfigurationtypes;

public class RetryExhaustedException extends RuntimeException {
    public RetryExhaustedException(String message, Throwable cause) {
        super(message, cause);
    }
}
