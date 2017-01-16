package com.zb.testing.stubbedservice.enums;

/**
 * Created by Zachery on 7/24/2016.
 */
public class VerificationMode {

    private final long minimumCalls;
    private final long maximumCalls;

    public VerificationMode(long minimumCalls, long maximumCalls) {
        this.minimumCalls = minimumCalls;
        this.maximumCalls = maximumCalls;
    }

    public long getMinimumCalls() {
        return minimumCalls;
    }

    public long getMaximumCalls() {
        return maximumCalls;
    }
}
