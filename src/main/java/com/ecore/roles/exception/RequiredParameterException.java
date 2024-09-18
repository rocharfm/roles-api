package com.ecore.roles.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

import static java.lang.String.format;

@ResponseStatus(value = HttpStatus.BAD_REQUEST)
public class RequiredParameterException extends RuntimeException {

    public RequiredParameterException(String parameter) {
        super(format("Required parameter %s is not present", parameter));
    }
}
