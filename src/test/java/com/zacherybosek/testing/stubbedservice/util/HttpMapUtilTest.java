package com.zacherybosek.testing.stubbedservice.util;

import com.zacherybosek.testing.stubbedservice.StubbedService;
import com.zacherybosek.testing.stubbedservice.exceptions.UnexpectedCallAssertionException;
import org.junit.After;
import org.junit.Test;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;

import static org.junit.Assert.assertEquals;

/**
 * Created by Zachery on 7/26/2016.
 */
public class HttpMapUtilTest {

    private StubbedService service9991 = new StubbedService(9991);

    @After
    public void after() {
        try {
            service9991.stop();
        } catch (UnexpectedCallAssertionException e) {
            //eat me
        }
    }
    @Test(expected = RestClientException.class)
    //TODO under the hood the util throws a null pointer.... fix it
    public void requestWithNullHeaderValue() {
        //given
        LinkedMultiValueMap<String, String> headers = new LinkedMultiValueMap<>();
        headers.add("foo", null);
        HttpEntity<String> entity = new HttpEntity<>("headers", headers);

        service9991.onRequestTo("/health").withHeaders(headers).giveResponse("not going to happen");

        service9991.start();

        //when
        RestTemplate restTemplate = new RestTemplate();
        ResponseEntity<String> response = restTemplate.exchange("http://localhost:9991/health", HttpMethod.GET, entity, String.class);
    }

    @Test
    public void requestWithCustomHeaderValue() {
        //given
        LinkedMultiValueMap<String, String> headers = new LinkedMultiValueMap<>();
        headers.add("foo", "bar");
        HttpEntity<String> entity = new HttpEntity<>("headers", headers);

        service9991.onRequestTo("/health").withHeaders(headers).giveResponse("is going to happen");

        service9991.start();

        //when
        RestTemplate restTemplate = new RestTemplate();
        ResponseEntity<String> response = restTemplate.exchange("http://localhost:9991/health", HttpMethod.GET, entity, String.class);
        assertEquals("is going to happen", response.getBody());
    }

    @Test
    public void requestWithCustomParameterValue() {
        //given
        LinkedMultiValueMap<String, String> params = new LinkedMultiValueMap<>();
        params.add("foo", "bar");

        service9991.onRequestTo("/health").withParams(params).giveResponse("is going to happen");

        service9991.start();

        //when
        RestTemplate restTemplate = new RestTemplate();
        ResponseEntity<String> response = restTemplate.getForEntity("http://localhost:9991/health?foo=bar", String.class);
        assertEquals("is going to happen", response.getBody());
    }

    @Test(expected = RestClientException.class)
    public void requestWithInvalidParameterValue() {
        //given
        LinkedMultiValueMap<String, String> params = new LinkedMultiValueMap<>();
        params.add("foo", "bar");

        service9991.onRequestTo("/health").withParams(params).giveResponse("is not going to happen");

        service9991.start();

        //when
        RestTemplate restTemplate = new RestTemplate();
        ResponseEntity<String> response = restTemplate.getForEntity("http://localhost:9991/health?foo=notatallbar", String.class);
    }
}
