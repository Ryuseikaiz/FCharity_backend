package fptu.fcharity.utils.exception;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import org.springframework.http.HttpStatus;

import java.time.ZonedDateTime;
@Getter
@Setter
@AllArgsConstructor
public class ApiException {
    private final HttpStatus httpStatus;
    private final String message;
    private final ZonedDateTime timestamp;


}
