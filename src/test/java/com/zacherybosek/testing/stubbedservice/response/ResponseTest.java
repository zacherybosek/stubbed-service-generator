package com.zacherybosek.testing.stubbedservice.response;

import com.zacherybosek.testing.stubbedservice.StubbedService;
import org.junit.After;
import org.junit.Assert;
import org.junit.Test;

/**
 * Created by Zachery on 7/26/2016.
 */
public class ResponseTest {

    private StubbedService stubbedService = new StubbedService(9999);

    @After
    public void tearDown() throws Exception {
        stubbedService.stop();
    }

    @Test
    public void singleParamResponse() throws Exception {
        Assert.fail();
    }

    @Test
    public void twoParamResponse() throws Exception {
        Assert.fail();
    }

    @Test
    public void threeParamResponse() throws Exception {
        Assert.fail();
    }

    @Test
    public void multipleResponses() throws Exception {
        Assert.fail();
    }

    @Test
    public void defaultResponseCalledMultipleTimes() throws Exception {
        Assert.fail();
    }
}
