package com.zacherybosek.testing.stubbedservice;

import com.zacherybosek.testing.stubbedservice.adapter.MockHandlerAdapter;
import com.zacherybosek.testing.stubbedservice.controller.MetricsController;
import com.zacherybosek.testing.stubbedservice.request.MockRequest;
import com.zacherybosek.testing.stubbedservice.request.SpringMockRequest;
import org.springframework.boot.context.embedded.AnnotationConfigEmbeddedWebApplicationContext;

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

    @Override
    public void close() throws Exception {
        stop();
    }

    public void stop() {
        if(!isInit || !isRunning) {
            throw new IllegalStateException("Service generator already shutdown");
        }
    }
}
