package cz.solutia.acme.exception;

public class SamePasswordException extends RuntimeException {
    public SamePasswordException() {
        super("password.same.as.old");
    }
}
