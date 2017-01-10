package com.zacherybosek.testing.stubbedservice;

import com.zacherybosek.testing.stubbedservice.adapter.MockHandlerAdapter;
import com.zacherybosek.testing.stubbedservice.controller.Generic404Controller;
import com.zacherybosek.testing.stubbedservice.controller.MetricsController;
import com.zacherybosek.testing.stubbedservice.enums.FilterType;
import com.zacherybosek.testing.stubbedservice.enums.MockResponseType;
import com.zacherybosek.testing.stubbedservice.enums.VerificationMode;
import com.zacherybosek.testing.stubbedservice.request.CoreMockRequest;
import com.zacherybosek.testing.stubbedservice.request.MockRequest;
import com.zacherybosek.testing.stubbedservice.request.SpringControllerMockRequest;
import com.zacherybosek.testing.stubbedservice.request.SpringMockRequest;
import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.beans.factory.support.RootBeanDefinition;
import org.springframework.boot.autoconfigure.PropertyPlaceholderAutoConfiguration;
import org.springframework.boot.autoconfigure.web.DispatcherServletAutoConfiguration;
import org.springframework.boot.autoconfigure.web.EmbeddedServletContainerAutoConfiguration;
import org.springframework.boot.autoconfigure.web.HttpMessageConverters;
import org.springframework.boot.context.embedded.AnnotationConfigEmbeddedWebApplicationContext;
import org.springframework.boot.context.embedded.ConfigurableEmbeddedServletContainer;
import org.springframework.boot.context.embedded.EmbeddedServletContainerCustomizer;
import org.springframework.http.converter.StringHttpMessageConverter;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;
import org.springframework.http.converter.xml.MappingJackson2XmlHttpMessageConverter;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.servlet.DispatcherServlet;
import org.springframework.web.servlet.HandlerMapping;
import org.springframework.web.servlet.mvc.method.annotation.RequestMappingHandlerMapping;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by Zachery on 7/24/2016.
 */
public class StubbedService implements AutoCloseable {

    int port;
    private AnnotationConfigEmbeddedWebApplicationContext childContext;
    private MockHandlerAdapter adapter = null;
    private boolean isRunning = false;
    private boolean isInit = false;
    private MetricsController metricsController;
    private SpringMockRequest generic404;
    private List<MockRequest> tmpMockRequests = new ArrayList<MockRequest>();
    private int requestLogSize = DEFAULT_REQUEST_LOG_SIZE;
    public static final int DEFAULT_REQUEST_LOG_SIZE = 10;

    public StubbedService(int port) {
        this.port = port;
    }

    public void setDefaultRequestLogSize(int requestLogSize) {
        this.requestLogSize = requestLogSize;
    }

    private void init() {
        if(isInit) {
            throw new IllegalStateException("Service generator already initialized");
        }
        isInit = true;
        initWebApplicationContext();
    }

    public void start() {
        if(isRunning) {
            throw new IllegalStateException("Already running");
        }
        tryInit();
        childContext.refresh();
        MockHandlerAdapter oldHandlerReference = adapter;
        adapter = childContext.getBean(MockHandlerAdapter.class);
        adapter.getMessageConverters().addAll(oldHandlerReference.getMessageConverters());

        adapter.setUnexpectedRequest(generic404);
        for(MockRequest mockRequest : tmpMockRequests) {
            CoreMockRequest coreMockRequest = (CoreMockRequest) mockRequest;
            coreMockRequest.setRequestLogSize(requestLogSize);
        }
        adapter.addRequests(tmpMockRequests);
        metricsController.setMockHandlerAdapter(adapter);

        RestTemplate restTemplate = new RestTemplate();
        try {
            restTemplate.getForObject("http://localhost:"+port+"/info", String.class);
        } catch (RestClientException e) {
            throw new IllegalStateException("Mock service failed to boot correctly");
        }

        isRunning = true;
    }

    public void stop() {
        if(!isInit || !isRunning) {
            throw new IllegalStateException("Service generator already shutdown");
        }

        childContext.close();
        isRunning = false;
        isInit = false;
        adapter.verifyAll();
    }

    @Override
    public void close() {
        stop();
    }

    private void tryInit() {
        if(!isInit) {
            init();
        }
    }

    private void initWebApplicationContext() {
        childContext = new AnnotationConfigEmbeddedWebApplicationContext();
        childContext.setId("StubbedService:"+port);

        HttpMessageConverters converters = new HttpMessageConverters(
                new StringHttpMessageConverter(),
                new MappingJackson2HttpMessageConverter(),
                new MappingJackson2XmlHttpMessageConverter()
        );

        HandlerMapping handlerMapping = new RequestMappingHandlerMapping();

        //makes sure any other configuration is not leaked down to the generator's config
        DispatcherServlet dispatcherServlet = new DispatcherServlet();
        dispatcherServlet.setDetectAllHandlerAdapters(false);
        dispatcherServlet.setDetectAllHandlerExceptionResolvers(false);
        dispatcherServlet.setDetectAllHandlerMappings(false);
        dispatcherServlet.setDetectAllViewResolvers(false);

        adapter = new MockHandlerAdapter();
        adapter.setMessageConverters(converters.getConverters());

        metricsController = new MetricsController();

        addSingleton(new PortEmbeddedServletContainerCustomizer(port));
        addSingleton(converters);
        addSingleton("dispatcherServlet", dispatcherServlet, true);
        addSingleton("handlerAdapter", adapter, true);
        addSingleton("handlerMapping", handlerMapping, true);
        addSingleton(metricsController);

        childContext.register(
                PropertyPlaceholderAutoConfiguration.class,
                EmbeddedServletContainerAutoConfiguration.class,
                DispatcherServletAutoConfiguration.class
        );

        generic404 = new SpringControllerMockRequest(childContext.getBeanFactory());
        generic404.giveResponse(Generic404Controller.class);
    }

    private void addSingleton(Object obj) {
        addSingleton(obj.getClass().getSimpleName(), obj, false);
    }

    private void addSingleton(String name, Object obj, boolean registerBeanDefinition) {
        ConfigurableBeanFactory beanFactory = childContext.getBeanFactory();
        if(registerBeanDefinition) {
            childContext.registerBeanDefinition(name, new RootBeanDefinition(obj.getClass()));
        } else {
            beanFactory.registerSingleton(name, obj);
        }
    }

    /**
     * A little inner class to customize the port the service will spin up on
     */
    private static class PortEmbeddedServletContainerCustomizer implements EmbeddedServletContainerCustomizer {

        private int port;

        public PortEmbeddedServletContainerCustomizer(int port) {
            this.port = port;
        }

        @Override
        public void customize(ConfigurableEmbeddedServletContainer configurableEmbeddedServletContainer) {
            configurableEmbeddedServletContainer.setPort(port);
        }
    }

    public SpringMockRequest onRequestTo(MockResponseType responseType) {
        tryInit();
        if(!isRunning) {
            SpringControllerMockRequest request = new SpringControllerMockRequest(childContext.getBeanFactory());
            request.setRequestLogSize(requestLogSize);
            tmpMockRequests.add(request);
            return request;
        }
        else {
            throw  new IllegalStateException("Cannot add controllers after app has already started");
        }
    }

    public MockRequest onRequestTo(String url) {
        tryInit();
        if(!isRunning) {
            CoreMockRequest request = new CoreMockRequest(url);
            request.setRequestLogSize(requestLogSize);
            tmpMockRequests.add(request);
            return request;
        } else {
            MockRequest request = adapter.onRequestTo(url);
            CoreMockRequest coreRequest = (CoreMockRequest) request;
            coreRequest.setRequestLogSize(requestLogSize);
            return coreRequest;
        }
    }

    public static final VerificationMode never() {
        return new VerificationMode(0,0);
    }

    public static final VerificationMode range(int min, int max) {
        return new VerificationMode(min, max);
    }

    public static final VerificationMode atLeast(int min) {
        return new VerificationMode(min, Long.MAX_VALUE);
    }

    public static final VerificationMode atMost(int max) {
        return new VerificationMode(0, max);
    }

    public static final VerificationMode times(int times) {
        return new VerificationMode(times, times);
    }

    public static final VerificationMode exactly(int times) {
        return new VerificationMode(times, times);
    }

    public static final MockResponseType controller() {
        return MockResponseType.CONTROLLER;
    }

    public static final FilterType any() {
        return FilterType.ANY;
    }

    public static final FilterType none() {
        return FilterType.NONE;
    }
}
