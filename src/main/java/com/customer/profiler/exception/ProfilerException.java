package com.customer.profiler.exception;

import java.io.Serializable;

public class ProfilerException  extends Exception implements Serializable {
    private static final long serialVersionUID = -3154618962130084535L;
    private String errorCode;
    private String errorType;
    private String description;

    public ProfilerException(String message){
        super(message);
    }

    public ProfilerException(String message,Throwable cause){
        super(message,cause);
    }

    public String getErrorCode() {
        return errorCode;
    }

    public void setErrorCode(String errorCode) {
        this.errorCode = errorCode;
    }

    public String getErrorType() {
        return errorType;
    }

    public void setErrorType(String errorType) {
        this.errorType = errorType;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }
}
