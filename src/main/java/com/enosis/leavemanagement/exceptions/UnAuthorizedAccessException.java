package com.enosis.leavemanagement.exceptions;

public class UnAuthorizedAccessException extends RuntimeException{
    public UnAuthorizedAccessException(String message){
        super(message);
    }
}
