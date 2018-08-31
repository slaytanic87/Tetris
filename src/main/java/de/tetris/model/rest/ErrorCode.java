package de.tetris.model.rest;

/**
 * @author Lam, Le (msg systems ag) 2018
 */
public enum ErrorCode {
    OK(0),
    NOK(1),
    INTERNAL_ERROR(2),
    UNKNOWN(3);

    private int code;

    ErrorCode(int code) {
        this.code = code;
    }

    public int getCode() {
        return this.code;
    }
}