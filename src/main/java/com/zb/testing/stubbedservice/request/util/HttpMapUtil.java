package com.zb.testing.stubbedservice.request.util;

import org.springframework.util.MultiValueMap;

import javax.servlet.http.HttpServletRequest;
import java.util.*;

/**
 * Created by Zachery on 7/24/2016.
 */
public class HttpMapUtil {

    public static boolean match(MultiValueMap<String, String> inMap, Map<String, String[]> httpMap) {
        for(String key : inMap.keySet()) {
            List<String> inValues = inMap.get(key);
            String[] httpValues = httpMap.get(key);

            boolean found = true;

            if(httpValues == null) {
                found = false;
            }
            else {
                for(String inMapValue : inValues) {
                    boolean isItemFound = false;
                    for(String httpValue : httpValues) {
                        if(inMapValue.equals(httpValue)) {
                            isItemFound = true;
                            break;
                        }
                    }
                    if(!isItemFound) {
                        found = false;
                    }
                }
            }
            if(!found) {
                return false;
            }
        }
        return true;
    }

    public enum HttpRequestType {
        URL_PARAMETERS, HTTP_HEADERS
    }

    public static Map<String, String[]> convertToMap(HttpServletRequest request, HttpRequestType httpRequestType) {
        if(httpRequestType == HttpRequestType.URL_PARAMETERS) {
            return request.getParameterMap();
        }
        else {
            return convertToMap(request);
        }
    }

    public static Map<String, String[]> convertToMap(HttpServletRequest headers) {
        Map<String, String[]> resultMap = new HashMap<>();
        Enumeration<String> headerNames = headers.getHeaderNames();

        while(headerNames.hasMoreElements()) {
            String headerName = headerNames.nextElement();
            Enumeration<String> headerValues = headers.getHeaders(headerName);
            List<String> newHeaderValues = new ArrayList<>();
            while(headerValues.hasMoreElements()) {
                String headerValue = headerValues.nextElement();
                newHeaderValues.add(headerValue);
            }
            String[] arrayHeaderValues = new String[newHeaderValues.size()];
            arrayHeaderValues = newHeaderValues.toArray(arrayHeaderValues);

            resultMap.put(headerName, arrayHeaderValues);
        }
        return resultMap;
    }

}
