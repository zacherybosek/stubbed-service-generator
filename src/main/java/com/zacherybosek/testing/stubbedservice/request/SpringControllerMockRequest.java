package com.zacherybosek.testing.stubbedservice.request;

import com.zacherybosek.testing.stubbedservice.enums.FilterType;
import com.zacherybosek.testing.stubbedservice.enums.VerificationMode;
import com.zacherybosek.testing.stubbedservice.request.info.InvokeInformation;
import org.springframework.beans.factory.config.ConfigurableListableBeanFactory;
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
public class SpringControllerMockRequest extends CoreMockRequest implements SpringMockRequest {

    private Object responseController;
    private ConfigurableListableBeanFactory beanFactory;

    public SpringControllerMockRequest(ConfigurableListableBeanFactory beanFactory) {
        super("");
        this.beanFactory = beanFactory;
    }

    @Override
    public SpringMockRequest giveResponse(String content) {
        throw new UnsupportedOperationException("Use spring MVC, for spring mock requests");
    }

    @Override
    public SpringMockRequest giveResponse(String content, MediaType contentType) {
        throw new UnsupportedOperationException("Use spring MVC, for spring mock requests");
    }

    @Override
    public SpringMockRequest giveResponse(String content, HttpStatus responseCode) {
        throw new UnsupportedOperationException("Use spring MVC, for spring mock requests");
    }

    @Override
    public SpringMockRequest giveResponse(String content, MediaType contentType, HttpStatus responseCode) {
        throw new UnsupportedOperationException("Use spring MVC, for spring mock requests");
    }

    @Override
    public SpringMockRequest withContentType(MediaType contentType) {
        throw new UnsupportedOperationException("Use spring MVC, for spring mock requests");
    }

    @Override
    public SpringMockRequest withMethod(HttpMethod method) {
        throw new UnsupportedOperationException("Use spring MVC, for spring mock requests");
    }

    @Override
    public SpringMockRequest withSleep(long sleepTime) {
        return (SpringControllerMockRequest) super.withSleep(sleepTime);
    }

    @Override
    public SpringMockRequest withRegEx() {
        throw new UnsupportedOperationException("Use spring MVC, for spring mock requests");
    }

    @Override
    public SpringMockRequest withRequestLogSize(int requestLogSize) {
        return (SpringControllerMockRequest)super.withRequestLogSize(requestLogSize);
    }

    @Override
    public SpringMockRequest withParams(FilterType parameterType) {
        throw new UnsupportedOperationException("Use spring MVC, for spring mock requests");
    }

    @Override
    public SpringMockRequest withParams(MultiValueMap<String, String> parameters) {
        throw new UnsupportedOperationException("Use spring MVC, for spring mock requests");
    }

    @Override
    public SpringMockRequest giveResponse(Object responseController) {
        this.responseController = responseController;
        beanFactory.registerSingleton(responseController.getClass().getSimpleName(), responseController);
        return this;
    }

    @Override
    public SpringMockRequest withHeaders(MultiValueMap<String, String> headers) {
        throw new UnsupportedOperationException("Use spring MVC, for spring mock requests");
    }

    @Override
    public SpringMockRequest withHeaders(FilterType headerType) {
        throw new UnsupportedOperationException("Use spring MVC, for spring mock requests");
    }

    @Override
    public SpringMockRequest verify(VerificationMode verifyMode) {
        return (SpringControllerMockRequest)super.verify(verifyMode);
    }

    @Override
    public SpringMockRequest giveResponse(Class<?> controllerClazz) {
        try {
            Object obj = controllerClazz.getConstructor().newInstance();
            return giveResponse(obj);
        } catch (Exception e) {
            throw new IllegalArgumentException("Input class needs a public constructor", e);
        }

    }

    @Override
    public ModelAndView invoke(HttpServletRequest request, HttpServletResponse response, HandlerMethod handlerMethod) {
        if(getSleepTime() > 0) {
            try {
                Thread.sleep(getSleepTime());
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
        InvokeInformation invokeInformation = new InvokeInformation(request,response);
        getInvokeInformation().add(invokeInformation);
        return null;
    }

    @Override
    public String getUrl() {
        return responseController.getClass().getSimpleName() +".class";
    }

    @Override
    public boolean matches(HttpServletRequest request, HandlerMethod handlerMethod) {
        return handlerMethod.getBean().equals(this.responseController);
    }
}
