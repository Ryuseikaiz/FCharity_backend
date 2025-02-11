package fptu.fcharity.exception;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.core.AuthenticationException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

import java.lang.reflect.InvocationTargetException;
import java.time.ZoneId;
import java.time.ZonedDateTime;
@ControllerAdvice
public class ApiRequestHandler {
    @ExceptionHandler(value = {ApiRequestException.class})
    public ResponseEntity<Object> handleApiRequestException(ApiRequestException e) {
        ApiException apiException = new ApiException(
               HttpStatus.BAD_REQUEST,e.getMessage(),
                 ZonedDateTime.now(ZoneId.of("Z"))
        );
        return new ResponseEntity<>(apiException, apiException.getHttpStatus());
    }
    @ExceptionHandler(value = { BadCredentialsException.class})
    public ResponseEntity<Object> handleBadCredentialsException(BadCredentialsException e) {
        ApiException apiException = new ApiException(
                HttpStatus.BAD_REQUEST,e.getMessage() +" password or email is incorrect",
                ZonedDateTime.now(ZoneId.of("Z"))
        );
        return new ResponseEntity<>(apiException, apiException.getHttpStatus());
    }
}
