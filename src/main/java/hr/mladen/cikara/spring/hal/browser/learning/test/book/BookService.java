package hr.mladen.cikara.spring.hal.browser.learning.test.book;

import java.util.Map;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface BookService {
  /**
   * Finds all Books in repository.
   *
   * @param pageable Pageable object
   * @return Page with books
   */
  Page<Book> findAll(Pageable pageable);

  /**
   * Finds all books with query string in title.
   *
   * @param query    String to find in title
   * @param pageable Pageable object
   * @return Page with books that confirm to query
   */
  Page<Book> findByTitleContaining(String query, Pageable pageable);

  /**
   * Saves book.
   *
   * @param book Book entity
   * @return created book
   */
  Book save(Book book);

  /**
   * Gets book from repository.
   *
   * @param bookId Book id
   * @return Book with given Id
   * @throws BookNotFoundException When book for id is not found
   */
  Book getBook(Long bookId) throws BookNotFoundException;

  /**
   * Deletes book from repository.
   *
   * @param bookId Book Id
   * @throws BookNotFoundException When book for id is not found
   */
  void deleteBook(Long bookId) throws BookNotFoundException;

  /**
   * Updates book on given id. Updates map can have keys that don't match fields in Book entity,
   * those field are ignored.
   *
   * @param bookId  Book id
   * @param updates Map with updates
   * @return Updated Book
   * @throws BookNotFoundException When book for id is not found
   */
  Book updateBook(Long bookId, Map<String, Object> updates) throws BookNotFoundException;

  /**
   * Replaces book on given id.
   *
   * @param bookId  Book id
   * @param bookDto BookDTO with values for book that should replace original book
   * @return Book that was saved in repository
   * @throws BookNotFoundException When book for id is not found
   */
  Book replaceBook(Long bookId, BookDto bookDto) throws BookNotFoundException;

  class BookNotFoundException extends Exception {
  }
}
