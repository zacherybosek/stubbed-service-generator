package com.zb.testing.stubbedservice.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestMapping;

/**
 * Created by Zachery on 7/25/2016.
 */
@RequestMapping("/**")
public class Generic404Controller {

    @Autowired
    private Environment env;

    @RequestMapping()
    public ResponseEntity<ErrorObject> health() {
        return new ResponseEntity<ErrorObject>(new ErrorObject("MockService returned 404"), HttpStatus.NOT_FOUND);
    }

    public static class ErrorObject {

        private String error;

        public ErrorObject(String error) {
            this.error = error;
        }

        public String getError() {
            return error;
        }

        public void setError(String error) {
            this.error = error;
        }
    }
}
