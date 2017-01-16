package com.zb.testing.stubbedservice.request;

import com.zb.testing.stubbedservice.enums.FilterType;
import org.junit.Test;
import org.springframework.beans.factory.config.ConfigurableListableBeanFactory;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Controller;
import org.springframework.util.LinkedMultiValueMap;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.mockito.Matchers.anyObject;
import static org.mockito.Matchers.anyString;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.mock;

/**
 * Created by Zachery on 7/26/2016.
 */
public class SpringRequestTest {

    @Test(expected = IllegalArgumentException.class)
    public void constructorErrorTest(){
        SpringMockRequest request = new SpringControllerMockRequest(null);
        request.giveResponse(TempClassBad.class);
    }

    @Test
    public void constructorPassTest() {
        ConfigurableListableBeanFactory mockBf = mock(ConfigurableListableBeanFactory.class);
        doNothing().when(mockBf).registerSingleton(anyString(), anyObject());
        SpringControllerMockRequest request = new SpringControllerMockRequest(mockBf);
        request.giveResponse(TempClassOK.class);
        assertEquals(TempClassOK.class.getSimpleName()+".class", request.getUrl());
    }
    public static class TempClassBad {
        public TempClassBad(Object obj) {
        }
    }

    @Controller
    public static class TempClassOK {
        private Object obj;
        public TempClassOK() {
            obj = new Object();
            obj.toString();
        }
    }

    @Test
    public void unimplementedTest() {
        SpringMockRequest request = new SpringControllerMockRequest(null);
        for(int i = 0; i <=10; i++) {
            boolean error = false;
            try {
                switch (i) {
                    case 0:
                        request.giveResponse("test");
                        break;
                    case 1:
                        request.giveResponse("test", MediaType.APPLICATION_JSON);
                        break;
                    case 2:
                        request.giveResponse("test", HttpStatus.BANDWIDTH_LIMIT_EXCEEDED);
                        break;
                    case 3:
                        request.giveResponse("test", MediaType.APPLICATION_JSON, HttpStatus.BANDWIDTH_LIMIT_EXCEEDED);
                        break;
                    case 4:
                        request.withContentType(MediaType.APPLICATION_JSON);
                        break;
                    case 5:
                        request.withMethod(HttpMethod.GET);
                        break;
                    case 6:
                        request.withRegEx();
                        break;
                    case 7:
                        request.withParams(FilterType.ANY);
                        break;
                    case 8:
                        request.withParams(new LinkedMultiValueMap<String, String>());
                        break;
                    case 9:
                        request.withHeaders(FilterType.ANY);
                        break;
                    case 10:
                        request.withHeaders(new LinkedMultiValueMap<String, String>());
                        break;
                }
            } catch (UnsupportedOperationException e) {
                error = true;
            }
            assertTrue("Unsupported exception was not thrown by scenario "+ i, error);
        }
    }
}
