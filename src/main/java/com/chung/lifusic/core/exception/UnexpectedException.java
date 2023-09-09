package com.chung.lifusic.core.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.server.ResponseStatusException;

public class UnexpectedException extends ResponseStatusException {
    public UnexpectedException() {
        super(HttpStatus.INTERNAL_SERVER_ERROR);
    }
}
