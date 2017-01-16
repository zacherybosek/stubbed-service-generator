package com.zacherybosek.testing.stubbedservice.info;

import com.zacherybosek.testing.stubbedservice.controller.MetricsController;
import com.zacherybosek.testing.stubbedservice.request.info.Information;
import com.zacherybosek.testing.stubbedservice.request.info.InvokeInformation;
import com.zacherybosek.testing.stubbedservice.request.info.SizeStoredSizeLimitedLinkedList;
import org.junit.Before;
import org.junit.Test;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import java.util.Map;

import static junit.framework.TestCase.assertTrue;
import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

/**
 * Created by Zachery on 7/26/2016.
 */
public class InformationTest {

    private Information infoShort;
    private Information infoLong;

    @Before
    public void before() {
        infoLong = new Information(MetricsController.InfoTypes.LONG);
        infoShort = new Information(MetricsController.InfoTypes.SHORT);

        assertEquals(MetricsController.InfoTypes.LONG, infoLong.getInfoType());
        assertEquals(MetricsController.InfoTypes.SHORT, infoShort.getInfoType());
    }
    //TODO nullSupportTest, unexpextedTEst, expectedTest

    @Test
    public void nullSupportTest() {
        infoLong.addExpectedInvoke(null);
        infoShort.addExpectedInvoke(null);

        infoLong.addUnexpectedInvoke(null);
        infoShort.addUnexpectedInvoke(null);
        assertTrue("Null pointer shouldn't throw any exceptions", true);
    }

    public InvokeInformation newInfo(String path, int statusCode) {
        return new MockInvokeInformation(path, statusCode);
    }

    @Test
    public void expectedTest() {
        SizeStoredSizeLimitedLinkedList<InvokeInformation> infos = new SizeStoredSizeLimitedLinkedList<>(5);
        infos.add(newInfo("/test", 200));
        infos.add(newInfo("/test2", 201));
        infos.add(newInfo("/test3", 200));
        infos.add(newInfo("/test4", 404));
        infoLong.addExpectedInvoke(infos);
        infoShort.addExpectedInvoke(infos);

        assertEquals(4, infoShort.getExpectedHits());
        assertEquals(0, infoShort.getExpectedCalls().size());

        Map<Integer, Integer> expectedCodeCounts = infoShort.getExpectedResponseCodeCounts();
        assertEquals(0, expectedCodeCounts.size());

        assertEquals(4, infoShort.getTotalHits());
        assertEquals(0, infoShort.getUnexpectedHits());
        assertEquals(0, infoShort.getUnexpectedCalls().size());
        Map<Integer, Integer> unexpectedCodeCounts = infoShort.getUnexpectedResponseCodeCounts();
        assertEquals(0, unexpectedCodeCounts.size());
    }

    @Test
    public void unexpectedTest() {
        SizeStoredSizeLimitedLinkedList<InvokeInformation> infos = new SizeStoredSizeLimitedLinkedList<>(5);
        infos.add(newInfo("/test", 200));
        infos.add(newInfo("/test2", 201));
        infos.add(newInfo("/test3", 200));
        infos.add(newInfo("/test4", 404));
        infoLong.addUnexpectedInvoke(infos);
        infoShort.addUnexpectedInvoke(infos);

        assertEquals(4, infoLong.getUnexpectedHits());
        assertEquals(4, infoLong.getUnexpectedCalls().size());
        assertEquals("/test4", infoLong.getUnexpectedCalls().get(3));

        Map<Integer, Integer> unexpectedCodeCounts = infoLong.getUnexpectedResponseCodeCounts();
        assertEquals(3, unexpectedCodeCounts.size());
        assertEquals(Integer.valueOf(1), unexpectedCodeCounts.get(201));
        assertEquals(Integer.valueOf(2), unexpectedCodeCounts.get(200));

        assertEquals(4, infoLong.getTotalHits());

        assertEquals(0, infoLong.getExpectedHits());
        assertEquals(0, infoLong.getExpectedCalls().size());
        Map<Integer, Integer> expectedCodeCounts = infoLong.getExpectedResponseCodeCounts();
        assertEquals(0, expectedCodeCounts.size());
    }

    private class MockInvokeInformation extends InvokeInformation {
        public MockInvokeInformation(String url, int statusCode) {
            super(getMockRequest(url), getMockResponse(statusCode));
        }
    }

    private static HttpServletRequest getMockRequest(String url) {
        HttpServletRequest request = mock(HttpServletRequest.class);
        String[] parts = url.split("[?]");
        String uri;
        String query;
        if(parts.length > 1) {
            uri = parts[0];
            query = parts[1];

        } else {
            uri = parts[0];
            query = null;
        }
        when(request.getRequestURI()).thenReturn(uri);
        when(request.getQueryString()).thenReturn(query);
        return request;
    }


    private static HttpServletResponse getMockResponse(int statusCode) {
        HttpServletResponse response = mock(HttpServletResponse.class);
        when(response.getStatus()).thenReturn(statusCode);
        return response;
    }
}