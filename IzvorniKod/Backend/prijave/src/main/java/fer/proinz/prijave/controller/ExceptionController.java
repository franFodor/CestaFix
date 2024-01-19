package fer.proinz.prijave.controller;

import fer.proinz.prijave.exception.*;
import org.aspectj.weaver.ast.Not;
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

    @ExceptionHandler(value = NonExistingUserException.class)
    @ResponseStatus(value = HttpStatus.NOT_FOUND)
    public String handleNonExistingUserException(final NonExistingUserException e) {
        return e.getMessage();
    }

    @ExceptionHandler(value = NonExistingCityDeptException.class)
    @ResponseStatus(value = HttpStatus.NOT_FOUND)
    public String handleNonExistingCityDeptException(final NonExistingCityDeptException e) {
        return e.getMessage();
    }

    @ExceptionHandler(value = NonExistingReportException.class)
    @ResponseStatus(value = HttpStatus.NOT_FOUND)
    public String handleNonExistingReportException(final NonExistingReportException e) {
        return e.getMessage();
    }

    @ExceptionHandler(value = NonExistingProblemException.class)
    @ResponseStatus(value = HttpStatus.NOT_FOUND)
    public String handleNonExistingProblemException(final NonExistingProblemException e) {
        return e.getMessage();
    }

    @ExceptionHandler(value = NonExistingPhotoException.class)
    @ResponseStatus(value = HttpStatus.NOT_FOUND)
    public String handleNonExistingPhotoException(final NonExistingPhotoException e) {
        return e.getMessage();
    }

    @ExceptionHandler(value = NotEnoughDataException.class)
    @ResponseStatus(value = HttpStatus.BAD_REQUEST)
    public String handleNotEnoughDataException(final NotEnoughDataException e) {
        return e.getMessage();
    }

}
