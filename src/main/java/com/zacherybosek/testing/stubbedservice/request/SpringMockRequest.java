package com.zacherybosek.testing.stubbedservice.request;

import com.zacherybosek.testing.stubbedservice.enums.FilterType;
import com.zacherybosek.testing.stubbedservice.enums.VerificationMode;
import com.zacherybosek.testing.stubbedservice.request.info.InvokeInformation;
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
public interface SpringMockRequest extends MockRequest {

    SpringMockRequest giveResponse(Class<?> controllerClazz);

    SpringMockRequest giveResponse(Object responseController);

    /**
     *
     * @param content Response body content
     *
     * @throws UnsupportedOperationException instance of implementing type
     * @return request
     */
    @Override
    MockRequest giveResponse(String content);

    @Override
    MockRequest giveResponse(String content, MediaType contentType);

    @Override
    MockRequest giveResponse(String content, HttpStatus responseCode);

    @Override
    MockRequest giveResponse(String content, MediaType contentType, HttpStatus responseCode);

    @Override
    MockRequest withContentType(MediaType contentType);

    @Override
    MockRequest withMethod(HttpMethod method);

    @Override
    MockRequest withSleep(long sleepTime);

    @Override
    MockRequest withRegEx();

    @Override
    MockRequest withRequestLogSize(int requestLogSize);

    @Override
    MockRequest withParams(FilterType parameterType);

    @Override
    MockRequest withParams(MultiValueMap<String, String> parameters);

    @Override
    MockRequest withHeaders(MultiValueMap<String, String> headers);

    @Override
    MockRequest withHeaders(FilterType headerType);

    @Override
    MockRequest verify(VerificationMode verifyMode);

    @Override
    ModelAndView invoke(HttpServletRequest request, HttpServletResponse response, HandlerMethod handlerMethod);

    @Override
    boolean matches(HttpServletRequest request, HandlerMethod handlerMethod);

    @Override
    SizeStoredSizeLimitedLinkedList<InvokeInformation> getInvokeInformation();
}
