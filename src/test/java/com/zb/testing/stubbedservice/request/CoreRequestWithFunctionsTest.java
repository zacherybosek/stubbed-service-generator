package com.zb.testing.stubbedservice.request;

import com.zb.testing.stubbedservice.StubbedService;
import org.junit.After;
import org.junit.Test;
import org.springframework.http.*;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.web.client.RestTemplate;

import static org.junit.Assert.assertEquals;

/**
 * Created by Zachery on 7/26/2016.
 */
public class CoreRequestWithFunctionsTest {

    private StubbedService service9991 = new StubbedService(9991);

    @Test
    public void requestWithPostMethod() throws Exception {
        //given
        service9991.onRequestTo("/health").withMethod(HttpMethod.POST)
                .giveResponse("<html><head></head><body>I made it</body></html>", MediaType.TEXT_HTML, HttpStatus.valueOf(202));
        service9991.start();
        RestTemplate restTemplate = new RestTemplate();

        //when
        Object param = "mock";
        Object request = "mock";
        ResponseEntity<String> response = restTemplate.postForEntity("http://localhost:9991/health", request, String.class, param);

        //then
        assertEquals("<html><head></head><body>I made it</body></html>", response.getBody());
        assertEquals(MediaType.TEXT_HTML_VALUE, response.getHeaders().getContentType().toString().split(";")[0]);
        assertEquals(HttpStatus.valueOf(202), response.getStatusCode());
    }

    @Test
    public void requestWithGetMethod() throws Exception {
        //given
        service9991.onRequestTo("/health").withMethod(HttpMethod.GET)
                .giveResponse("<html><head></head><body>I made it</body></html>", MediaType.TEXT_HTML, HttpStatus.valueOf(202));
        service9991.start();
        RestTemplate restTemplate = new RestTemplate();

        //when
        ResponseEntity<String> response = restTemplate.getForEntity("http://localhost:9991/health", String.class);

        //then
        assertEquals("<html><head></head><body>I made it</body></html>", response.getBody());
        assertEquals(MediaType.TEXT_HTML_VALUE, response.getHeaders().getContentType().toString().split(";")[0]);
        assertEquals(HttpStatus.valueOf(202), response.getStatusCode());
    }

    @Test
    public void requestWithContentType() throws Exception {
        //given
        service9991.onRequestTo("/health").withContentType(MediaType.TEXT_HTML)
                .giveResponse("<html><head></head><body>I made it</body></html>", MediaType.TEXT_HTML, HttpStatus.valueOf(202));
        service9991.start();
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.TEXT_HTML);
        HttpEntity<String> entity = new HttpEntity<>("headers", headers);
        RestTemplate restTemplate = new RestTemplate();

        //when
        ResponseEntity<String> response = restTemplate.exchange("http://localhost:9991/health", HttpMethod.POST, entity, String.class);

        //then
        assertEquals("<html><head></head><body>I made it</body></html>", response.getBody());
        assertEquals(MediaType.TEXT_HTML_VALUE, response.getHeaders().getContentType().toString().split(";")[0]);
        assertEquals(HttpStatus.valueOf(202), response.getStatusCode());
    }

    @Test
    public void requestWithMethodAndContentType() throws Exception {
        //given
        service9991.onRequestTo("/health").withMethod(HttpMethod.POST).withContentType(MediaType.TEXT_HTML)
                .giveResponse("<html><head></head><body>I made it</body></html>", MediaType.TEXT_HTML, HttpStatus.valueOf(202));
        service9991.start();
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.TEXT_HTML);
        HttpEntity<String> entity = new HttpEntity<>("headers", headers);
        RestTemplate restTemplate = new RestTemplate();

        //when
        ResponseEntity<String> response = restTemplate.exchange("http://localhost:9991/health", HttpMethod.POST, entity, String.class);

        //then
        assertEquals("<html><head></head><body>I made it</body></html>", response.getBody());
        assertEquals(MediaType.TEXT_HTML_VALUE, response.getHeaders().getContentType().toString().split(";")[0]);
        assertEquals(HttpStatus.valueOf(202), response.getStatusCode());
    }

    @Test
    public void requestWithAnyQueryStringParams() throws Exception {
        //given
        service9991.onRequestTo("/health").withParams(StubbedService.any())
                .giveResponse("hello");
        service9991.start();
        RestTemplate restTemplate = new RestTemplate();

        //when
        ResponseEntity<String> response = restTemplate.getForEntity("http://localhost:9991/health", String.class);

        //then
        assertEquals("hello", response.getBody());
        assertEquals(MediaType.ALL_VALUE, response.getHeaders().getContentType().toString().split(";")[0]);
        assertEquals(HttpStatus.valueOf(200), response.getStatusCode());
    }

    @Test
    public void requestWithNoneQueryStringParams() throws Exception {
        //given
        service9991.onRequestTo("/health").withParams(StubbedService.none())
                .giveResponse("hello");
        service9991.start();
        RestTemplate restTemplate = new RestTemplate();

        //when
        ResponseEntity<String> response = restTemplate.getForEntity("http://localhost:9991/health", String.class);

        //then
        assertEquals("hello", response.getBody());
        assertEquals(MediaType.ALL_VALUE, response.getHeaders().getContentType().toString().split(";")[0]);
        assertEquals(HttpStatus.valueOf(200), response.getStatusCode());
    }

    @Test
    public void requestWithEmptyMapQueryStringParams() throws Exception {
        LinkedMultiValueMap<String, String> params = new LinkedMultiValueMap<>();
        //given
        service9991.onRequestTo("/health").withParams(params)
                .giveResponse("hello");
        service9991.start();
        RestTemplate restTemplate = new RestTemplate();

        //when
        ResponseEntity<String> response = restTemplate.getForEntity("http://localhost:9991/health", String.class);

        //then
        assertEquals("hello", response.getBody());
        assertEquals(MediaType.ALL_VALUE, response.getHeaders().getContentType().toString().split(";")[0]);
        assertEquals(HttpStatus.valueOf(200), response.getStatusCode());
    }

    @Test
    public void requestWithCustomQueryStringParams() throws Exception {
        LinkedMultiValueMap<String, String> params = new LinkedMultiValueMap<>();
        params.add("hello", "world");
        params.add("foo", "bar");
        //given
        service9991.onRequestTo("/health").withParams(params)
                .giveResponse("hello");
        service9991.start();
        RestTemplate restTemplate = new RestTemplate();

        //when
        ResponseEntity<String> response = restTemplate.getForEntity("http://localhost:9991/health?hello=world&foo=bar&key=value", String.class);

        //then
        assertEquals("hello", response.getBody());
        assertEquals(MediaType.ALL_VALUE, response.getHeaders().getContentType().toString().split(";")[0]);
        assertEquals(HttpStatus.valueOf(200), response.getStatusCode());
    }

    @Test
    public void requestWithAnyHeadersParams() throws Exception {
        //given
        service9991.onRequestTo("/health").withHeaders(StubbedService.any())
                .giveResponse("<html><head></head><body>I made it</body></html>");
        service9991.start();
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.TEXT_HTML);
        HttpEntity<String> entity = new HttpEntity<>("headers", headers);
        RestTemplate restTemplate = new RestTemplate();

        //when
        ResponseEntity<String> response = restTemplate.exchange("http://localhost:9991/health", HttpMethod.POST, entity, String.class);

        //then
        assertEquals("<html><head></head><body>I made it</body></html>", response.getBody());
        assertEquals(HttpStatus.valueOf(200), response.getStatusCode());
    }

    @Test
    public void requestWithNoneHeaderParams() throws Exception {
        //given
        service9991.onRequestTo("/health").withHeaders(StubbedService.none())
                .giveResponse("<html><head></head><body>I made it</body></html>");
        service9991.start();
        HttpHeaders headers = new HttpHeaders();
        HttpEntity<String> entity = new HttpEntity<>("headers", headers);
        RestTemplate restTemplate = new RestTemplate();

        //when
        ResponseEntity<String> response = restTemplate.exchange("http://localhost:9991/health", HttpMethod.POST, entity, String.class);

        //then
        assertEquals("<html><head></head><body>I made it</body></html>", response.getBody());
        assertEquals(HttpStatus.valueOf(200), response.getStatusCode());
    }

    @After
    public void after() {
        service9991.stop();
    }
}
