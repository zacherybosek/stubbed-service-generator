package com.zb.testing.stubbedservice.response;

import com.zb.testing.stubbedservice.StubbedService;
import org.junit.After;
import org.junit.Test;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.RestTemplate;

import static org.junit.Assert.assertEquals;

/**
 * Created by Zachery on 7/26/2016.
 */
public class ResponseTest {

    private StubbedService stubbedService = new StubbedService(9999);

    @After
    public void tearDown() throws Exception {
        stubbedService.stop();
    }

    @Test
    public void singleParamResponse() throws Exception {
        //given
        stubbedService.onRequestTo("/health")
                .giveResponse("hello");
        stubbedService.start();
        RestTemplate restTemplate = new RestTemplate();

        //when
        ResponseEntity<String> response = restTemplate.getForEntity("http://localhost:9999/health", String.class);

        //then
        assertEquals("hello", response.getBody());
    }

    @Test
    public void twoParamResponse() throws Exception {
        //given
        stubbedService.onRequestTo("/health")
                .giveResponse("hello", HttpStatus.PARTIAL_CONTENT);
        stubbedService.start();
        RestTemplate restTemplate = new RestTemplate();

        //when
        ResponseEntity<String> response = restTemplate.getForEntity("http://localhost:9999/health", String.class);

        //then
        assertEquals("hello", response.getBody());
        assertEquals(HttpStatus.PARTIAL_CONTENT, response.getStatusCode());
    }

    @Test
    public void threeParamResponse() throws Exception {
        //given
        stubbedService.onRequestTo("/health")
                .giveResponse("hello", MediaType.APPLICATION_JSON, HttpStatus.ACCEPTED);
        stubbedService.start();
        RestTemplate restTemplate = new RestTemplate();

        //when
        ResponseEntity<String> response = restTemplate.getForEntity("http://localhost:9999/health", String.class);

        //then
        assertEquals("hello", response.getBody());
        assertEquals(HttpStatus.ACCEPTED, response.getStatusCode());
        assertEquals(MediaType.APPLICATION_JSON_VALUE, response.getHeaders().getContentType().toString().split(";")[0]);
    }

    @Test
    public void multipleResponses() throws Exception {
        //given
        stubbedService.onRequestTo("/health")
                .giveResponse("hello").giveResponse("world");
        stubbedService.start();
        RestTemplate restTemplate = new RestTemplate();

        //when
        ResponseEntity<String> response = restTemplate.getForEntity("http://localhost:9999/health", String.class);

        //then the first response should be returned
        assertEquals("hello", response.getBody());

        //when again
        response = restTemplate.getForEntity("http://localhost:9999/health", String.class);

        //then the second response should be returned
        assertEquals("world", response.getBody());
    }

    @Test
    public void defaultResponseCalledMultipleTimes() throws Exception {
        //given
        stubbedService.onRequestTo("/health")
                .giveResponse("hello");
        stubbedService.start();
        RestTemplate restTemplate = new RestTemplate();

        //when
        ResponseEntity<String> response = restTemplate.getForEntity("http://localhost:9999/health", String.class);

        //then
        assertEquals("hello", response.getBody());

        //when again
        response = restTemplate.getForEntity("http://localhost:9999/health", String.class);

        //then the result should be the same
        assertEquals("hello", response.getBody());
    }
}
