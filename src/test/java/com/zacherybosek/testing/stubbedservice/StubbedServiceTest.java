package com.zacherybosek.testing.stubbedservice;

import com.zacherybosek.testing.stubbedservice.enums.MockResponseType;
import org.junit.After;
import org.junit.Test;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.client.RestTemplate;

import static org.junit.Assert.assertEquals;

/**
 * Created by Zachery on 7/25/2016.
 */
public class StubbedServiceTest {
    private StubbedService service9999 = new StubbedService(9999);
    private StubbedService service9998 = new StubbedService(9998);
    private StubbedService service9997 = new StubbedService(9997);

    @Test
    public void test() throws Exception {
        service9999.onRequestTo(MockResponseType.CONTROLLER).giveResponse(new ChildController());
        service9998.onRequestTo(MockResponseType.CONTROLLER).giveResponse(MainController.class);
        service9999.start();
        service9998.start();

        service9997.onRequestTo("/health")
                .giveResponse("Helllowwwww");
        service9997.start();

        RestTemplate restTemplate = new RestTemplate();
        String result9999 = restTemplate.getForObject("http://localhost:9999/health", String.class);
        String result9998 = restTemplate.getForObject("http://localhost:9998/health", String.class);
        String result9997 = restTemplate.getForObject("http://localhost:9997/health", String.class);

        assertEquals("Helllowwwww", result9997);
        assertEquals("child!", result9999);
        assertEquals("main!", result9998);
    }

    @After
    public void after() {
        service9997.stop();
        service9998.stop();
        service9999.stop();
    }

    @Controller
    public static class ChildController {

        @RequestMapping(value="/health", method = RequestMethod.GET, produces = MediaType.TEXT_PLAIN_VALUE)
        public @ResponseBody String health() {
            return "child!";
        }
    }

    @Controller
    public static class MainController {

        @RequestMapping(value="/health", method = RequestMethod.GET, produces = MediaType.TEXT_PLAIN_VALUE)
        public @ResponseBody String health() {
            return "main!";
        }
    }
}
