package cz.solutia.acme.exception;

public class InvalidCurrentPasswordException extends RuntimeException {
    public InvalidCurrentPasswordException() { super("password.current.invalid"); }
}

