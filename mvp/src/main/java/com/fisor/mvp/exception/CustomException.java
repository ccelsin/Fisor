package com.fisor.mvp.exception;

import lombok.Getter;

public final class CustomException extends Exception{
    
    @Getter
    public static class UnauthorizedException extends RuntimeException {
        public UnauthorizedException(String message) {
            super(message);
        }
    }
    
}
