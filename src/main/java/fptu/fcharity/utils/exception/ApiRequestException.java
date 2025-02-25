package fptu.fcharity.utils.exception;

import org.springframework.security.core.AuthenticationException;

import java.lang.reflect.InvocationTargetException;

public class ApiRequestException extends  RuntimeException {
    public ApiRequestException(String message) {
        super(message);
    }
}
