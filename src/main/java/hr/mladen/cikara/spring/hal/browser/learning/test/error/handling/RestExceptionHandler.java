package hr.mladen.cikara.spring.hal.browser.learning.test.error.handling;

import hr.mladen.cikara.spring.hal.browser.learning.test.book.BookService;
import hr.mladen.cikara.spring.hal.browser.learning.test.book.BooksControllerImpl;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
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
  protected ResponseEntity<Object> handleEntityNotFound(BookService.BookNotFoundException ex) {
    ApiError apiError = new ApiError(HttpStatus.NOT_FOUND, ex);
    apiError.setMessage("Couldn't find book with id " + ex.getBookId());

    return buildResponseEntity(apiError);
  }

  @ExceptionHandler({BooksControllerImpl.WrongMethodUsedForCreatingBookException.class})
  protected ResponseEntity<Object> handleWrongMethodUsedForCreatingBook(
          BooksControllerImpl.WrongMethodUsedForCreatingBookException ex) {
    ApiError apiError = new ApiError(HttpStatus.NOT_FOUND, ex);
    apiError.setMessage("Please use POST method for creating new record");

    return buildResponseEntity(apiError);
  }

  @Override
  protected ResponseEntity<Object> handleNoHandlerFoundException(
          NoHandlerFoundException ex, HttpHeaders headers, HttpStatus status, WebRequest request) {

    ApiError apiError = new ApiError(HttpStatus.NOT_FOUND);
    apiError.setMessage("Resource not found " + ex.getRequestURL());

    return buildResponseEntity(apiError);
  }

  private ResponseEntity<Object> buildResponseEntity(ApiError apiError) {
    return new ResponseEntity<>(apiError, apiError.getStatus());
  }
}
