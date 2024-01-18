package fer.proinz.prijave.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.NOT_FOUND)
public class NonExistingCityDeptException extends Exception {

    public NonExistingCityDeptException() {
        super("City Department not found.");
    }

}
