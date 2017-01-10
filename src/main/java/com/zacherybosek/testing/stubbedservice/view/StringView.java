package com.zacherybosek.testing.stubbedservice.view;

import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.web.servlet.View;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.Map;

/**
 * Created by Zachery on 7/24/2016.
 */
public class StringView implements View {

    private MediaType contentType;
    private String response;
    private HttpStatus responseCode;

    public StringView(String response, MediaType contentType, HttpStatus responseCode) {
        this.contentType = contentType;
        this.response = response;
        this.responseCode = responseCode;
    }

    @Override
    public String getContentType() {
        return contentType.toString();
    }

    public int getResponseCode() {
        return responseCode.value();
    }

    public String getResponse() {
        return response;
    }

    @Override
    public void render(Map<String, ?> map, HttpServletRequest httpServletRequest, HttpServletResponse httpServletResponse) throws Exception {
        httpServletResponse.setStatus(getResponseCode());
        httpServletResponse.setContentType(getContentType());
        httpServletResponse.getOutputStream().print(getResponse());
    }
}
