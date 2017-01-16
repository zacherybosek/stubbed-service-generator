package com.zb.testing.stubbedservice.response;

import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;

/**
 * Created by Zachery on 7/24/2016.
 */
public class Response {

    private String responseContent;
    private MediaType responseContentType;
    private HttpStatus responseCode;

    private Response(String responseContent, MediaType responseContentType, HttpStatus responseCode) {
        this.responseContent = responseContent;
        this.responseContentType = responseContentType;
        this.responseCode = responseCode;
    }

    public static Response getResponse(String responseContent, MediaType responseContentType, HttpStatus responseCode) {
        return new Response(responseContent, responseContentType, responseCode);
    }

    public String getResponseContent() {
        return responseContent;
    }

    public MediaType getResponseContentType() {
        return responseContentType;
    }

    public HttpStatus getResponseCode() {
        return responseCode;
    }
}
