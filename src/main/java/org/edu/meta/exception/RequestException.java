package org.edu.meta.exception;

/**
 * @author scott
 * @since 27.04.2022
 */
public class RequestException extends RuntimeException {

    private static final int ERROR_CODE = 1;
    private final Integer code;


    public RequestException(String message) {
        super(message);
        this.code = ERROR_CODE;
    }

    public RequestException(Integer code) {
        this.code = code;
    }

    public RequestException(Integer code, String message) {
        super(message);
        this.code = code;
    }

    public Integer getCode() {
        return code;
    }
}
