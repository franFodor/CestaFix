package fer.proinz.prijave.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.NOT_FOUND)
public class NonExistingUserException extends Exception {

    public NonExistingUserException() {
        super("User doesn't exist.");
    }

}
