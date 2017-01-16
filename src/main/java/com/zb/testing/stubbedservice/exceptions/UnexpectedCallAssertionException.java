package com.zb.testing.stubbedservice.exceptions;

/**
 * Created by Zachery on 7/25/2016.
 */
public class UnexpectedCallAssertionException extends AssertionError {

    public UnexpectedCallAssertionException() {
        super();
    }

    public UnexpectedCallAssertionException(String message) {
        super(message);
    }

    public UnexpectedCallAssertionException(String message, Throwable cause) {
        super(message, cause);
    }

    public UnexpectedCallAssertionException(Throwable cause) {
        super(cause);
    }
}
