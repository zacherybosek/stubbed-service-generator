package com.zb.testing.stubbedservice.request.info;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * Created by Zachery on 7/24/2016.
 */
public class InvokeInformation {

    private String incomingUrl;
    private int responseCode;

    public InvokeInformation(HttpServletRequest request, HttpServletResponse response) {
        this.incomingUrl = buildIncomingUrl(request);
        this.responseCode = response.getStatus();
    }

    private String buildIncomingUrl(HttpServletRequest request) {
        String requestURL = request.getRequestURI();
        String queryString = request.getQueryString();

        if(queryString == null) {
            return requestURL.toString();
        } else {
            return requestURL + "?" + queryString;
        }
    }

    public String getIncomingUrl() {
        return incomingUrl;
    }

    public int getResponseCode() {
        return responseCode;
    }
}
