package io.github.regularcommands.commands;

/**
 * Exception thrown by the stylizer when it is given some sort of invalid input.
 */
public class BadFormatException extends RuntimeException {
    public BadFormatException(String message) { super(message); }
}