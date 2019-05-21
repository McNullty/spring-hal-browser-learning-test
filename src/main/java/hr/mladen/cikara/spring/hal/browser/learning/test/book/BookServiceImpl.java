package hr.mladen.cikara.spring.hal.browser.learning.test.book;

import java.util.Map;
import java.util.Optional;
import lombok.AccessLevel;
import lombok.Getter;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.Assert;

@Service
@Getter(AccessLevel.PRIVATE)
@Transactional
public class BookServiceImpl implements BookService {

  private final BookRepository bookRepository;

  public BookServiceImpl(final BookRepository bookRepository) {
    Assert.notNull(bookRepository, "Service can't work without repository");
    this.bookRepository = bookRepository;
  }

  @Transactional(readOnly = true)
  @Override
  public Page<Book> findAll(final Pageable pageable) {
    return bookRepository.findAll(pageable);
  }

  @Transactional(readOnly = true)
  @Override
  public Page<Book> findByTitleContaining(final String query, final Pageable pageable) {
    return bookRepository.findByTitleContaining(query, pageable);
  }

  @Override
  public Book save(final Book book) {
    return bookRepository.save(book);
  }

  @Transactional(readOnly = true)
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

    if (updates.containsKey("title")) {
      builder.title((String) updates.get("title"));
    }

    if (updates.containsKey("author")) {
      builder.author((String) updates.get("author"));
    }

    if (updates.containsKey("blurb")) {
      builder.blurb((String) updates.get("blurb"));
    }

    if (updates.containsKey("pages")) {
      builder.pages((Integer) updates.get("pages"));
    }

    return builder.build();
  }
}
