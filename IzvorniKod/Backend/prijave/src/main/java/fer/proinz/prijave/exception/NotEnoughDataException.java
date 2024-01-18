package fer.proinz.prijave.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.BAD_REQUEST)
public class NotEnoughDataException extends Exception {

    public NotEnoughDataException() {
        super("No address, photo or coordinates given.");
    }

}
