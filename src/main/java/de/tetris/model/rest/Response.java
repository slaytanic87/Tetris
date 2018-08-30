package de.tetris.model.rest;

import lombok.Data;

/**
 * @author Lam, Le (msg systems ag) 2018
 */
@Data
public class Response {
    private String message;
    private ErrorCode code;
}
