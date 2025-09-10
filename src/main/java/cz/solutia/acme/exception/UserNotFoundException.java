package cz.solutia.acme.exception;

public class UserNotFoundException extends RuntimeException {
    public UserNotFoundException(String u) { super("user.not.found:" + u); }
}