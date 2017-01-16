package com.zb.testing.stubbedservice.request;

import com.zb.testing.stubbedservice.StubbedService;
import com.zb.testing.stubbedservice.request.info.InvokeInformation;
import com.zb.testing.stubbedservice.request.info.SizeStoredSizeLimitedLinkedList;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.invocation.InvocationOnMock;
import org.mockito.runners.MockitoJUnitRunner;
import org.mockito.stubbing.Answer;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.web.method.HandlerMethod;

import javax.servlet.ServletOutputStream;
import javax.servlet.WriteListener;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.security.InvalidParameterException;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.mockito.Matchers.anyInt;
import static org.mockito.Matchers.anyString;
import static org.mockito.Mockito.doAnswer;
import static org.mockito.Mockito.when;

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

    @Before
    public void before() {
        when(request.getMethod()).thenReturn("GET");
        when(request.getRequestURI()).thenReturn(SIMPLE_TEST_DEFAULT_URL);
    }

    @Test
    public void testString() {
        CoreMockRequest cmr = new CoreMockRequest("/test.html");

        when(request.getRequestURI()).thenReturn("/nottest");
        assertFalse(cmr.matches(request,handlerMethod));

        when(request.getRequestURI()).thenReturn("/test.html");
        assertTrue(cmr.matches(request, handlerMethod));
    }

    @Test
    public void testRegEx() {
        CoreMockRequest cmr = new CoreMockRequest("/test.+");
        cmr.withRegEx();

        when(request.getRequestURI()).thenReturn("/test");
        assertFalse(cmr.matches(request, handlerMethod));

        when(request.getRequestURI()).thenReturn("/test.html");
        assertTrue(cmr.matches(request, handlerMethod));
    }

    @Test
    public void testGlob() {
        CoreMockRequest cmr = new CoreMockRequest("/test*");

        when(request.getRequestURI()).thenReturn("/tes");
        assertFalse(cmr.matches(request, handlerMethod));

        when(request.getRequestURI()).thenReturn("/test.html");
        assertTrue(cmr.matches(request, handlerMethod));
    }

    @Test
    public void testResponse() throws Exception {
        CoreMockRequest cmr = new CoreMockRequest(SIMPLE_TEST_DEFAULT_URL);
        cmr.giveResponse("test1");
        cmr.giveResponse("test2");
        cmr.giveResponse("test3", HttpStatus.ACCEPTED);
        cmr.giveResponse("test4", MediaType.APPLICATION_XML);
        cmr.giveResponse("test5", MediaType.APPLICATION_JSON, HttpStatus.IM_USED);

        final ServletOutputStreamImpl stream = new ServletOutputStreamImpl();
        final Temp<Integer> lastStatus = new Temp<>();
        final Temp<String> lastContentType = new Temp<>();

        when(response.getOutputStream()).thenReturn(stream);

        //record the setStatus value inside of lastStatus
        doAnswer(new Answer<Object>() {
            @Override
            public Object answer(InvocationOnMock invocation) throws Throwable {
                Object[] args = invocation.getArguments();
                Integer val = (Integer) args[0];
                lastStatus.setVal(val);
                return null;
            }
        }).when(response).setStatus(anyInt());

        //record the setContentType value inside of lastContentType
        doAnswer(new Answer<Object>() {
            @Override
            public Object answer(InvocationOnMock invocation) throws Throwable {
                Object[] args = invocation.getArguments();
                String val = (String) args[0];
                lastContentType.setVal(val);
                return null;
            }
        }).when(response).setContentType(anyString());

        stream.reset();
        cmr.invoke(request, response, handlerMethod).getView().render(null, request,response);
        assertEquals("test1", stream.toString());

        stream.reset();
        cmr.invoke(request, response, handlerMethod).getView().render(null, request,response);
        assertEquals("test2", stream.toString());

        stream.reset();
        cmr.invoke(request, response, handlerMethod).getView().render(null, request,response);
        assertEquals("test3", stream.toString());
        assertEquals(HttpStatus.ACCEPTED.value(), lastStatus.getVal().intValue());

        stream.reset();
        cmr.invoke(request, response, handlerMethod).getView().render(null, request,response);
        assertEquals("test4", stream.toString());
        assertEquals(MediaType.APPLICATION_XML_VALUE, lastContentType.getVal());

        stream.reset();
        cmr.invoke(request, response, handlerMethod).getView().render(null, request,response);
        assertEquals("test5", stream.toString());
        assertEquals(HttpStatus.IM_USED.value(), lastStatus.getVal().intValue());
        assertEquals(MediaType.APPLICATION_JSON_VALUE, lastContentType.getVal());

        assertTrue(cmr.matches(request, handlerMethod));
    }

    @Test
    public void testSleep() {
        long sleepTime = 100;
        CoreMockRequest cmr = new CoreMockRequest(SIMPLE_TEST_DEFAULT_URL);
        cmr.withSleep(sleepTime);
        long startTime = System.currentTimeMillis();

        cmr.giveResponse("test");
        cmr.invoke(request, response, handlerMethod);

        long endTime = System.currentTimeMillis();
        long diffTime = endTime - startTime;

        assertTrue("Sleep only slept for "+diffTime+" milli seconds. expected: " + sleepTime, diffTime >= sleepTime);
    }

    @Test
    public void testContentType() {
        CoreMockRequest cmr = new CoreMockRequest(SIMPLE_TEST_DEFAULT_URL);
        cmr.withContentType(MediaType.APPLICATION_JSON);

        when(request.getContentType()).thenReturn(MediaType.APPLICATION_JSON_VALUE);
        assertTrue(cmr.matches(request, handlerMethod));

        when(request.getContentType()).thenReturn(MediaType.APPLICATION_XML_VALUE);
        assertFalse(cmr.matches(request, handlerMethod));
    }

    @Test
    public void testUrl() {
        CoreMockRequest cmr = new CoreMockRequest(SIMPLE_TEST_DEFAULT_URL);
        assertEquals(SIMPLE_TEST_DEFAULT_URL, cmr.getUrl());
    }

    @Test
    public void testLogSize() {
        int requestLogSize = 1;
        CoreMockRequest cmr = new CoreMockRequest(SIMPLE_TEST_DEFAULT_URL);

        cmr.setRequestLogSize(5);
        cmr.withRequestLogSize(requestLogSize);
        cmr.setRequestLogSize(5);
        cmr.giveResponse("test");
        cmr.invoke(request, response, handlerMethod);
        cmr.invoke(request, response, handlerMethod);
        cmr.invoke(request, response, handlerMethod);

        SizeStoredSizeLimitedLinkedList<InvokeInformation> invokeInformation = cmr.getInvokeInformation();
        int requestSize = 0;
        for(InvokeInformation ii : invokeInformation) {
            requestSize++;
        }

        //because the withRequestLogSize was called, the setRequestLogSize method should be ignored.
        //returns a three, as it was called three times. But only a single url is actually persisted.
        assertEquals(3, invokeInformation.size());
        assertEquals(1, requestSize);
    }

    public enum VerifyScenario {
        NEVER, ATLEAST, EXACTLY, TIMES, ATMOSR, RANGE
    }

    @Test
    public void testVerify() {
        CoreMockRequest cmr = new CoreMockRequest(SIMPLE_TEST_DEFAULT_URL);
        cmr.giveResponse("test");

        cmr.verifyRequest();

        for(VerifyScenario vs: VerifyScenario.values()) {
            switch (vs) {
                case ATLEAST:
                    cmr.verify(StubbedService.atLeast(0));
                    break;
                case ATMOSR:
                    cmr.verify(StubbedService.atMost(0));
                    break;
                case EXACTLY:
                    cmr.verify(StubbedService.exactly(0));
                    break;
                case NEVER:
                    cmr.verify(StubbedService.never());
                    break;
                case TIMES:
                    cmr.verify(StubbedService.times(0));
                    break;
                case RANGE:
                    cmr.verify(StubbedService.range(0,1));
                    break;
                default:
                    break;
            }
            cmr.verifyRequest();
        }

        cmr.invoke(request, response, handlerMethod);

        for(VerifyScenario vs: VerifyScenario.values()) {
            switch (vs) {
                case ATLEAST:
                    cmr.verify(StubbedService.atLeast(2));
                    break;
                case ATMOSR:
                    cmr.verify(StubbedService.atMost(0));
                    break;
                case EXACTLY:
                    cmr.verify(StubbedService.exactly(5));
                    break;
                case NEVER:
                    cmr.verify(StubbedService.never());
                    break;
                case TIMES:
                    cmr.verify(StubbedService.times(8));
                    break;
                case RANGE:
                    cmr.verify(StubbedService.range(3,5));
                    break;
                default:
                    break;
            }
            boolean errorFound = false;
            try {
                cmr.verifyRequest();
            }catch (AssertionError e) {
                errorFound = true;
            }
            assertTrue("Scenario "+vs+" should have thrown an Assertion error, as the incorrect number of call occured.", errorFound);
        }

    }

    @Test
    public void testNegativeSleep() {
        long sleepTime = -100;
        CoreMockRequest cmr = new CoreMockRequest(SIMPLE_TEST_DEFAULT_URL);
        cmr.withSleep(sleepTime);
        long startTime = System.currentTimeMillis();

        cmr.giveResponse("test");
        cmr.invoke(request, response, handlerMethod);

        long endTime = System.currentTimeMillis();
        long diffTime = endTime - startTime;

        assertTrue("Sleep only slept for "+diffTime+" milli seconds. expected: " + sleepTime, diffTime >= sleepTime);

    }

    public static class Temp<E> {
        private E val;
        public E getVal() {
            return val;
        }

        public void setVal(E val) {
            this.val = val;
        }
    }

    public static class ServletOutputStreamImpl extends ServletOutputStream {

        private ByteArrayOutputStream bos = new ByteArrayOutputStream();

        @Override
        public boolean isReady() {
            return true;
        }

        @Override
        public void setWriteListener(WriteListener writeListener) {
            throw new InvalidParameterException("Not implemented");
        }

        @Override
        public void write(int b) throws IOException {
            bos.write(b);
        }

        public void reset() {
            bos.reset();
        }

        @Override
        public String toString() {
            String str = new String(bos.toByteArray());
            return str;
        }
    }
}
