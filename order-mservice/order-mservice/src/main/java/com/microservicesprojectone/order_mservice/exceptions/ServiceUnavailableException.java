package com.microservicesprojectone.order_mservice.exceptions;

public class ServiceUnavailableException extends RuntimeException{
    public ServiceUnavailableException(String message){
        super(message);
    }
}
