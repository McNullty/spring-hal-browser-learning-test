package hr.mladen.cikara.spring.hal.browser.learning.test.error.handling;

import hr.mladen.cikara.spring.hal.browser.learning.test.book.BookService;
import hr.mladen.cikara.spring.hal.browser.learning.test.book.BooksController;
import hr.mladen.cikara.spring.hal.browser.learning.test.security.user.UserService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.servlet.NoHandlerFoundException;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

@Order(Ordered.HIGHEST_PRECEDENCE)
@ControllerAdvice
@Slf4j
public class RestExceptionHandler extends ResponseEntityExceptionHandler {

  @ExceptionHandler(BookService.BookNotFoundException.class)
  protected ResponseEntity<Object> handleBookNotFound(BookService.BookNotFoundException ex) {
    ApiError apiError = new ApiError(HttpStatus.NOT_FOUND, ex);
    apiError.setMessage("Couldn't find book with id " + ex.getBookId());

    return buildResponseEntity(apiError);
  }

  @ExceptionHandler(UserService.UserNotFoundException.class)
  protected ResponseEntity<Object> handleUserNotFound(UserService.UserNotFoundException ex) {
    ApiError apiError = new ApiError(HttpStatus.NOT_FOUND, ex);
    apiError.setMessage("Couldn't find user with id " + ex.getUserId());

    return buildResponseEntity(apiError);
  }

  @ExceptionHandler({BooksController.WrongMethodUsedForCreatingBookException.class})
  protected ResponseEntity<Object> handleWrongMethodUsedForCreatingBook(
          BooksController.WrongMethodUsedForCreatingBookException ex) {
    ApiError apiError = new ApiError(HttpStatus.NOT_FOUND, ex);
    apiError.setMessage("Please use POST method for creating new record");

    return buildResponseEntity(apiError);
  }

  @ExceptionHandler({UserService.UsernameAlreadyTakenException.class})
  protected ResponseEntity<Object> handleUsernameAlreadyTakenException(
          UserService.UsernameAlreadyTakenException ex) {
    ApiError apiError = new ApiError(HttpStatus.BAD_REQUEST, ex);
    apiError.setMessage("Username " + ex.getUsername() + " is already taken");

    return buildResponseEntity(apiError);
  }

  @ExceptionHandler({UserService.PasswordsDontMatch.class})
  protected ResponseEntity<Object> handlePasswordsDontMatch(
          UserService.PasswordsDontMatch ex) {
    ApiError apiError = new ApiError(HttpStatus.BAD_REQUEST, ex);
    apiError.setMessage("Passwords dont match");

    return buildResponseEntity(apiError);
  }

  @Override
  protected ResponseEntity<Object> handleNoHandlerFoundException(
          NoHandlerFoundException ex, HttpHeaders headers, HttpStatus status, WebRequest request) {

    ApiError apiError = new ApiError(HttpStatus.NOT_FOUND);
    apiError.setMessage("Resource not found " + ex.getRequestURL());

    return buildResponseEntity(apiError);
  }

  /**
   * Handle MethodArgumentNotValidException. Triggered when an object fails @Valid validation.
   *
   * @param ex      the MethodArgumentNotValidException that is thrown when @Valid validation fails
   * @param headers HttpHeaders
   * @param status  HttpStatus
   * @param request WebRequest
   * @return the ApiError object
   */
  @Override
  protected ResponseEntity<Object> handleMethodArgumentNotValid(
          MethodArgumentNotValidException ex,
          HttpHeaders headers,
          HttpStatus status,
          WebRequest request) {
    ApiError apiError = new ApiError(HttpStatus.BAD_REQUEST);
    apiError.setMessage("Validation error");
    apiError.addValidationErrors(ex.getBindingResult().getFieldErrors());
    apiError.addValidationError(ex.getBindingResult().getGlobalErrors());
    return buildResponseEntity(apiError);
  }

  /**
   * Handles javax.validation.ConstraintViolationException. Thrown when @Validated fails.
   *
   * @param ex the ConstraintViolationException
   * @return the ApiError object
   */
  @ExceptionHandler(javax.validation.ConstraintViolationException.class)
  protected ResponseEntity<Object> handleConstraintViolation(
          javax.validation.ConstraintViolationException ex) {
    ApiError apiError = new ApiError(HttpStatus.BAD_REQUEST);
    apiError.setMessage("Validation error");
    apiError.addValidationErrors(ex.getConstraintViolations());
    return buildResponseEntity(apiError);
  }

  private ResponseEntity<Object> buildResponseEntity(ApiError apiError) {
    return new ResponseEntity<>(apiError, apiError.getStatus());
  }
}
