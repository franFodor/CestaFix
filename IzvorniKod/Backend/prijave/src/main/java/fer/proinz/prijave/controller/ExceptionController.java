package fer.proinz.prijave.controller;

import fer.proinz.prijave.exception.NonExistingCategoryException;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;

@ControllerAdvice
public class ExceptionController {

    @ExceptionHandler(value = NonExistingCategoryException.class)
    @ResponseStatus(value = HttpStatus.NOT_FOUND)
    public String handleNonExistingCategoryException(final NonExistingCategoryException e) {
        return e.getMessage();
    }

}
