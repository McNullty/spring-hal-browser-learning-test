package hr.mladen.cikara.spring.hal.browser.learning.test.book;

import java.util.Map;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PagedResourcesAssembler;
import org.springframework.hateoas.PagedResources;
import org.springframework.http.ResponseEntity;

public interface BooksController {

  /**
   * Endpoint for listing all books.
   *
   * @param pageable  Pageable object, injected by Spring
   * @param assembler ResourcesAssembler object, injected by Spring
   * @return List of books
   */
  ResponseEntity<PagedResources<BookResource>> findAll(
          Pageable pageable, PagedResourcesAssembler<Book> assembler);

  /**
   * Searching for books that in titla have query string.
   *
   * @param query     Query that must match in book title
   * @param pageable  Pageable object, injected by Spring
   * @param assembler ResourcesAssembler object, injected by Spring
   * @return List of books that match query
   */
  ResponseEntity<PagedResources<BookResource>> search(
          String query, Pageable pageable, PagedResourcesAssembler<Book> assembler);

  /**
   * Endpoint for creating new Book.
   *
   * @param bookDto Book DTO
   * @return returns HTTP 201 Created
   */
  ResponseEntity<?> createBook(BookDto bookDto);

  /**
   * Endpoint for getting book details.
   *
   * @param bookId Book Id
   * @return Book details
   */
  ResponseEntity<BookResource> getBook(Long bookId) throws BookService.BookNotFoundException;

  /**
   * Endpoint for deleting book.
   *
   * @param bookId Book Id
   * @return Returns HTTP 204
   */
  ResponseEntity<?> deleteBook(Long bookId) throws BookService.BookNotFoundException;

  /**
   * Endpoint for updating book data.
   *
   * @param updates Map with update data
   * @param bookId  Book Id
   * @return Returns HTTP 204
   */
  ResponseEntity<?> updateBook(Map<String, Object> updates, Long bookId)
          throws BookService.BookNotFoundException;

  /**
   * Endpoint for replacing books.
   *
   * @param bookDto Book DTO
   * @param bookId  Book Id
   * @return Returns HTTP 204
   */
  ResponseEntity<?> replaceBook(BookDto bookDto, Long bookId)
          throws BooksController.WrongMethodUsedForCreatingBookException;

  class WrongMethodUsedForCreatingBookException extends Exception {
  }
}
