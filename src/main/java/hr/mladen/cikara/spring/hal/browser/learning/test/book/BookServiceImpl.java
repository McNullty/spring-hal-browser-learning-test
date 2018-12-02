package hr.mladen.cikara.spring.hal.browser.learning.test.book;

import java.util.Map;
import java.util.Optional;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.util.Assert;

@Service
public class BookServiceImpl implements BookService {

  private final BookRepository bookRepository;

  public BookServiceImpl(final BookRepository bookRepository) {
    Assert.notNull(bookRepository, "Service can't work without repository");
    this.bookRepository = bookRepository;
  }

  @Override
  public Page<Book> findAll(final Pageable pageable) {
    return bookRepository.findAll(pageable);
  }

  @Override
  public Page<Book> findByTitleContaining(final String query, final Pageable pageable) {
    return bookRepository.findByTitleContaining(query, pageable);
  }

  @Override
  public Book save(final Book book) {
    return bookRepository.save(book);
  }

  @Override
  public Book getBook(final Long bookId) throws BookNotFoundException {
    try {
      Optional<Book> book = bookRepository.findById(bookId);

      if (book.isPresent()) {
        return book.get();
      } else {
        throw new BookNotFoundException(bookId);
      }
    } catch (IllegalArgumentException e) {
      throw new BookNotFoundException(bookId);
    }

  }

  @Override
  public void deleteBook(final Long bookId) throws BookNotFoundException {
    try {
      Optional<Book> book = bookRepository.findById(bookId);

      if (book.isPresent()) {
        bookRepository.delete(book.get());
      } else {
        throw new BookNotFoundException(bookId);
      }
    } catch (IllegalArgumentException e) {
      throw new BookNotFoundException(bookId);
    }
  }

  @Override
  public Book updateBook(
          final Long bookId, final Map<String, Object> updates) throws BookNotFoundException {
    Optional<Book> book = bookRepository.findById(bookId);

    if (book.isPresent()) {
      Book updatedBook = applyChanges(book.get(), updates);

      return bookRepository.save(updatedBook);
    } else {
      throw new BookNotFoundException(bookId);
    }
  }

  @Override
  public Book replaceBook(final Long bookId, final BookDto bookDto) throws BookNotFoundException {
    Optional<Book> book = bookRepository.findById(bookId);

    if (book.isPresent()) {
      return bookRepository.save(bookDto.getBook(bookId));
    } else {
      throw new BookNotFoundException(bookId);
    }
  }

  private Book applyChanges(final Book book, final Map<String, Object> updates) {

    Book.BookBuilder builder = Book.builder();

    builder
            .id(book.getId())
            .title(book.getTitle())
            .author(book.getAuthor())
            .blurb(book.getBlurb())
            .pages(book.getPages());

    for (Map.Entry<String, Object> entry : updates.entrySet()) {
      switch (entry.getKey().toLowerCase()) {
        case "title":
          builder.title((String) entry.getValue());
          break;
        case "author":
          builder.author((String) entry.getValue());
          break;
        case "blurb":
          builder.blurb((String) entry.getValue());
          break;
        case "pages":
          builder.pages((Integer) entry.getValue());
          break;
        default:
          break;
      }
    }

    return builder.build();

  }
}
