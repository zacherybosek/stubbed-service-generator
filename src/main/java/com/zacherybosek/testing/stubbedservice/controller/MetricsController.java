package com.zacherybosek.testing.stubbedservice.controller;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.zacherybosek.testing.stubbedservice.adapter.MockHandlerAdapter;
import com.zacherybosek.testing.stubbedservice.request.MockRequest;
import com.zacherybosek.testing.stubbedservice.request.info.Information;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

/**
 * Created by Zachery on 7/24/2016.
 */
@Controller
@RequestMapping("/info")
public class MetricsController {

    @Autowired
    private Environment env;

    public enum InfoTypes {

        SHORT, LONG;

        public static InfoTypes getInfoType(String type) {
            if(type != null && (type.toUpperCase().equals("LONG") || type.toUpperCase().equals("FULL"))) {
                return InfoTypes.LONG;
            } else {
                return InfoTypes.SHORT;
            }
        }
    }

    private MockHandlerAdapter adapter;

    public void setMockHandlerAdapter(MockHandlerAdapter adapter) {
        this.adapter = adapter;
    }

    @RequestMapping(method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
    public @ResponseBody
    Information info(String type) throws JsonProcessingException {
        Information info = new Information(InfoTypes.getInfoType(type));
        for(MockRequest mockRequest: adapter.getMockRequests()) {
            info.addExpectedInvoke(mockRequest.getInvokeInformation());
        }

        if(adapter.getUnexpectedRequest() != null) {
            info.addUnexpectedInvoke(adapter.getUnexpectedRequest().getInvokeInformation());
        }
        return info;
    }
}
