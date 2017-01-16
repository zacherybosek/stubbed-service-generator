package com.zb.testing.stubbedservice.adapter;

import com.zb.testing.stubbedservice.controller.MetricsController;
import com.zb.testing.stubbedservice.exceptions.UnexpectedCallAssertionException;
import com.zb.testing.stubbedservice.request.CoreMockRequest;
import com.zb.testing.stubbedservice.request.MockRequest;
import org.springframework.web.method.HandlerMethod;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.method.annotation.RequestMappingHandlerAdapter;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by Zachery on 7/24/2016.
 */
public class MockHandlerAdapter extends RequestMappingHandlerAdapter {

    public MockHandlerAdapter() {
        super();
    }

    private List<MockRequest> mockRequests = new ArrayList<>();
    private MockRequest unexpectedRequest;

    @Override
    protected ModelAndView handleInternal(HttpServletRequest request, HttpServletResponse response, HandlerMethod handlerMethod) throws Exception {
        for(MockRequest mockRequest: mockRequests) {
            if(mockRequest.matches(request, handlerMethod)) {
                ModelAndView mav = mockRequest.invoke(request, response, handlerMethod);
                if(mav == null) {
                    return super.handleInternal(request, response, handlerMethod);
                } else {
                    return mav;
                }
            }
        }
        if(unexpectedRequest != null && !handlerMethod.getBean().getClass().equals(MetricsController.class)) {
            unexpectedRequest.invoke(request, response, handlerMethod);
        }
        return super.handleInternal(request, response, handlerMethod);
    }

    public MockRequest onRequestTo(String url) {
        MockRequest request = new CoreMockRequest(url);
        mockRequests.add(request);
        return request;
    }

    public void verifyAll() throws AssertionError {
        for(MockRequest mockRequest: mockRequests) {
            CoreMockRequest coreMockRequest = (CoreMockRequest) mockRequest;
            coreMockRequest.verifyRequest();
        }
        long unexpectdCount = unexpectedRequest.getInvokeInformation().size();
        if(unexpectdCount > 0) {
            throw new UnexpectedCallAssertionException("There was "+unexpectdCount+" unexpected calls made");
        }
    }

    public void addRequests(List<MockRequest> tmpMockRequests) {
        this.mockRequests.addAll(tmpMockRequests);
    }

    public void setUnexpectedRequest(MockRequest unexpectedRequest) {
        this.unexpectedRequest = unexpectedRequest;
    }

    public List<MockRequest> getMockRequests() {
        return mockRequests;
    }

    public MockRequest getUnexpectedRequest() {
        return unexpectedRequest;
    }
}
