package com.zacherybosek.testing.stubbedservice.request;

import com.zacherybosek.testing.stubbedservice.request.info.InvokeInformation;
import com.zacherybosek.testing.stubbedservice.enums.FilterType;
import com.zacherybosek.testing.stubbedservice.enums.VerificationMode;
import com.zacherybosek.testing.stubbedservice.request.info.SizeStoredSizeLimitedLinkedList;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.util.MultiValueMap;
import org.springframework.web.method.HandlerMethod;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * Created by Zachery on 7/24/2016.
 */
public interface MockRequest {

    /**
     * The response will contain a customized response body.
     *
     * @param content Response body content
     * @return MockRequest Instance of implementing type
     */
    MockRequest giveResponse(String content);

    MockRequest giveResponse(String content, MediaType contentType);

    MockRequest giveResponse(String content, HttpStatus responseCode);

    MockRequest giveResponse(String content, MediaType contentType, HttpStatus responseCode);

    MockRequest withContentType(MediaType contentType);

    MockRequest withMethod(HttpMethod method);

    MockRequest withSleep(long sleepTime);

    MockRequest withRegEx();

    MockRequest withRequestLogSize(int requestLogSize);

    MockRequest withParams(FilterType parameterType);

    MockRequest withParams(MultiValueMap<String, String> parameters);

    MockRequest withHeaders(MultiValueMap<String, String> headers);

    MockRequest withHeaders(FilterType headerType);

    MockRequest verify(VerificationMode verifyMode);

    ModelAndView invoke(HttpServletRequest request, HttpServletResponse response, HandlerMethod handlerMethod);

    boolean matches(HttpServletRequest request, HandlerMethod handlerMethod);

    SizeStoredSizeLimitedLinkedList<InvokeInformation> getInvokeInformation();
}
