package com.nad.multiads.gdpr;

import androidx.annotation.NonNull;

public class ConsentError {
    private final int errorCode;
    private final String message;

    public ConsentError(int errorCode, String message) {
        this.errorCode = errorCode;
        this.message = message;
    }

    public int getErrorCode() {
        return errorCode;
    }

    public String getMessage() {
        return message;
    }

    @NonNull
    @Override
    public String toString() {
        return "ConsentError{errorCode=" + errorCode + ", message='" + message + "'}";
    }
}
