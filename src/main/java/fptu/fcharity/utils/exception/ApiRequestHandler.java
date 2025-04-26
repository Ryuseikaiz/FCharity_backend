package fptu.fcharity.utils.exception;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

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
    @ExceptionHandler(value = { IllegalArgumentException.class})
    public ResponseEntity<Object> handleIllegalArgumentException(IllegalArgumentException e) {
        ApiException apiException = new ApiException(
                HttpStatus.BAD_REQUEST,e.getMessage(),
                ZonedDateTime.now(ZoneId.of("Z"))
        );
        return new ResponseEntity<>(apiException, apiException.getHttpStatus());
    }
    @ExceptionHandler(value = { Exception.class })
    public ResponseEntity<Object> handleGenericException(Exception e) {
        ApiException apiException = new ApiException(
                HttpStatus.INTERNAL_SERVER_ERROR,
                "Unexpected error: " + e.getMessage(),
                ZonedDateTime.now(ZoneId.of("Z"))
        );
        return new ResponseEntity<>(apiException, apiException.getHttpStatus());
    }

}
