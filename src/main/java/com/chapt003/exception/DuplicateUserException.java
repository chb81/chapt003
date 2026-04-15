package com.chapt003.exception;

public class DuplicateUserException extends BusinessException {
    public DuplicateUserException(String message) {
        super(10001, message);
    }

    public DuplicateUserException(String fieldName, String value) {
        super(10001, fieldName + " " + value + " 已被使用");
    }
}