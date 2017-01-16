package com.zb.testing.stubbedservice;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.springframework.boot.test.TestRestTemplate;
import org.springframework.http.*;
import org.springframework.stereotype.Controller;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.client.RestTemplate;

import static org.junit.Assert.assertEquals;

/**
 * Created by Zachery on 7/25/2016.
 */
public class SampleUsageTest {

    StubbedService stubbedService;

    @Before
    public void before() {
        stubbedService = new StubbedService(9999);
    }

    @After
    public void after() {
        stubbedService.close();
    }

    @Test
    public void simpleUsage() {

        //pass in a url to respond to, and specify a response
        stubbedService
                .onRequestTo("/sample/url")
                .giveResponse("A Sample Response!");

        //start the service
        stubbedService.start();

        // call to prove the service worked
        RestTemplate sampleCall = new RestTemplate();
        String results = sampleCall.getForObject("http://localhost:9999/sample/url", String.class);
        assertEquals("A Sample Response!", results);
    }

    @Test
    public void advancedUsage() {
        stubbedService
                // respond if the url (no parameters, no host information) matches the input string
                .onRequestTo("[/].*")
                // enables the incoming url to be a regular expression, to match multiple urls
                .withRegEx()
                // only respond to requests with a content type of text/plain
                .withContentType(MediaType.TEXT_PLAIN)
                // respond if ANY header is passed in. NONE is also acceptable.
                .withHeaders(StubbedService.any())
                // only respond if the service matches the headers in the incoming MultiValueMap
                .withHeaders(new LinkedMultiValueMap<String, String>())
                //respond if ANY parameter is passed in. None is also acceptable.
                .withParams(StubbedService.any())
                //only respond if the service matches the parameters in the incoming MultiValueMap
                .withParams(new LinkedMultiValueMap<String, String>())
                //only respond to a post
                .withMethod(HttpMethod.POST)
                //logs the last 100 calls, so they appear on the /info page
                .withRequestLogSize(100)
                //adds a delay to every response, to simulate a slow service
                .withSleep(100)
                //adds a verification step to confirm this service was called the correct amount of times.
                //never, times, range, atLEat, atMost, and exactly are allowed
                .verify(StubbedService.times(5))

                /**
                 * The service can return different responses for each call it
                 * will return responses in the order they are configured, the
                 * last response configured will be returned for additional calls
                 */

                //1st call - return a simple string
                .giveResponse("sample content")

                //2nd call - return xml content, with a XML media type
                .giveResponse("<sample>Content</sample>", MediaType.APPLICATION_XML)

                //3rd call - return a 500 status with an error
                .giveResponse("BOOOOM!!!", HttpStatus.INTERNAL_SERVER_ERROR)

                //4th call - (and all additional calls) return a json 200 message
                .giveResponse("{ json:\"fun\" }", MediaType.APPLICATION_JSON, HttpStatus.CREATED);


        //start the service
        stubbedService.start();

        //set up the request headers
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.TEXT_PLAIN);
        HttpEntity<String> entity = new HttpEntity<>("headers", headers);

        //setup a call to the configured service
        RestTemplate sampleCall = new TestRestTemplate();
        //when
        ResponseEntity<String> response = sampleCall.exchange("http://localhost:9999/sample/url", HttpMethod.POST, entity, String.class);
        //then
        assertEquals("sample content", response.getBody());
        //when
        ResponseEntity<String> response2 = sampleCall.exchange("http://localhost:9999/sample/url", HttpMethod.POST, entity, String.class);
        //then
        assertEquals("<sample>Content</sample>", response2.getBody());
        //when
        ResponseEntity<String> response3 = sampleCall.exchange("http://localhost:9999/sample/url", HttpMethod.POST, entity, String.class);
        //then
        assertEquals("BOOOOM!!!", response3.getBody());
        assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, response3.getStatusCode());
        //when
        ResponseEntity<String> response4 = sampleCall.exchange("http://localhost:9999/sample/url", HttpMethod.POST, entity, String.class);
        //then
        assertEquals("{ json:\"fun\" }", response4.getBody());
        assertEquals(HttpStatus.CREATED, response4.getStatusCode());
        //when
        ResponseEntity<String> response5 = sampleCall.exchange("http://localhost:9999/sample/url", HttpMethod.POST, entity, String.class);
        //then
        assertEquals("{ json:\"fun\" }", response5.getBody());
        assertEquals(HttpStatus.CREATED, response5.getStatusCode());
    }

    @Test
    public void springUsage() {

        //if a spring controller is requested, simply say its a controller
        stubbedService.onRequestTo(StubbedService.controller())

                //sleep, verify, and request log work the same
                .withSleep(10)
                .withRequestLogSize(100)
                .verify(StubbedService.exactly(1))

                //and pass in a controller
                .giveResponse(SampleSpringController.class);

        // spring will do the rest. This is to enable code reuse of existing services
        // and or advanced configurations and dynamic response
        // NOTE: spring does all the work. configuration outside of spring is not supported
        stubbedService.start();

        RestTemplate sampleCall = new RestTemplate();
        //when
        String results = sampleCall.getForObject("http://localhost:9999/test", String.class);
        assertEquals("controller results", results);
    }

    @Controller
    public static class SampleSpringController {

        @RequestMapping(value = "/test", method = RequestMethod.GET, produces = MediaType.TEXT_PLAIN_VALUE)
        public @ResponseBody String url() {
            return "controller results";
        }
    }

}
