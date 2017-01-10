package com.zacherybosek.testing.stubbedservice.request;

import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;
import org.springframework.web.method.HandlerMethod;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * Created by Zachery on 7/26/2016.
 */
@RunWith(MockitoJUnitRunner.class)
public class CoreRequestTest {

    @Mock
    private HttpServletRequest request;

    @Mock
    private HttpServletResponse response;

    @Mock
    private HandlerMethod handlerMethod;

    private static final String SIMPLE_TEST_DEFAULT_URL = "/test";
}
