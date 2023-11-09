package com.chung.lifusic.musicservice.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.server.ResponseStatusException;

public class FileStorageException extends ResponseStatusException {
    public FileStorageException() {
        super(HttpStatus.INTERNAL_SERVER_ERROR);
    }
    public FileStorageException(String reason) {
        super(HttpStatus.INTERNAL_SERVER_ERROR, reason);
    }
}
