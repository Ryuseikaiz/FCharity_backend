package fptu.fcharity.exception;

import org.springframework.security.core.AuthenticationException;

import java.lang.reflect.InvocationTargetException;

public class ApiRequestException extends  RuntimeException {
    public ApiRequestException(String message) {
        super(message);
    }
    public ApiRequestException(String message,Throwable cause) {
        super(message,cause);
    }
}
