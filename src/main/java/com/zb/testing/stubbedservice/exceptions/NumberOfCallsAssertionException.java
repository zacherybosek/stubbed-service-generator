package com.zb.testing.stubbedservice.exceptions;

/**
 * Created by Zachery on 7/24/2016.
 */
public class NumberOfCallsAssertionException extends AssertionError {
    public NumberOfCallsAssertionException() {
        super();
    }

    public NumberOfCallsAssertionException(String message) {
        super(message);
    }

    public NumberOfCallsAssertionException(String message, Throwable cause) {
        super(message, cause);
    }

    public NumberOfCallsAssertionException(Throwable cause) {
        super(cause);
    }

}
