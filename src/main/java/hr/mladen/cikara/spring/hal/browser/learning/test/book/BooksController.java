package hr.mladen.cikara.spring.hal.browser.learning.test.book;

import hr.mladen.cikara.spring.hal.browser.learning.test.ConversionToJsonException;
import java.net.URI;
import java.util.Map;
import java.util.Optional;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PagedResourcesAssembler;
import org.springframework.hateoas.ExposesResourceFor;
import org.springframework.hateoas.Link;
import org.springframework.hateoas.MediaTypes;
import org.springframework.hateoas.PagedResources;
import org.springframework.hateoas.mvc.ControllerLinkBuilder;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@RestController
@RequestMapping("/books")
@ExposesResourceFor(Book.class)
public class BooksController {

  private final BookRepository bookRepository;
  private final BookToBookResourceAssembler bookToBookResourceAssembler;

  public BooksController(
          final BookRepository bookRepository,
          final BookToBookResourceAssembler bookToBookResourceAssembler) {
    this.bookRepository = bookRepository;
    this.bookToBookResourceAssembler = bookToBookResourceAssembler;
  }

  /**
   * Endpoint for listing all books.
   *
   * @param pageable  Pageable object, injected by Spring
   * @param assembler ResourcesAssembler object, injected by Spring
   * @return List of books
   */
  @GetMapping(produces = {MediaTypes.HAL_JSON_VALUE})
  public ResponseEntity<PagedResources<BookResource>> findAll(
          final Pageable pageable,
          final PagedResourcesAssembler<Book> assembler) {

    Page<Book> books = bookRepository.findAll(pageable);

    PagedResources<BookResource> booksPagedResources =
            getPagedBookResourcesWithLinks(assembler, books);


    return ResponseEntity.ok(booksPagedResources);
  }

  /**
   * Searching for books that in titla have query string.
   *
   * @param query Query that must match in book title
   * @param pageable Pageable object, injected by Spring
   * @param assembler ResourcesAssembler object, injected by Spring
   * @return List of books that match query
   */
  @GetMapping(value = "/search/title-contains", produces = {MediaTypes.HAL_JSON_VALUE})
  public ResponseEntity<PagedResources<BookResource>> search(
          @RequestParam(name = "query") final String query,
          final Pageable pageable, final PagedResourcesAssembler<Book> assembler) {

    Page<Book> books = bookRepository.findByTitleContaining(query, pageable);

    PagedResources<BookResource> booksPagedResources =
            getPagedBookResourcesWithLinks(assembler, books);

    return ResponseEntity.ok(booksPagedResources);
  }

  /**
   * Endpoint for creating new Book.
   *
   * @param bookDto Book DTO
   * @return returns HTTP 201 Created
   */
  @PostMapping(consumes = MediaTypes.HAL_JSON_VALUE, produces = {MediaTypes.HAL_JSON_VALUE})
  public ResponseEntity<?> createBook(@RequestBody BookDto bookDto) {
    log.debug("Got book: {}", bookDto);

    Book book = bookRepository.save(bookDto.getBook());


    BookResource bookResource = bookToBookResourceAssembler.toResource(book);

    return ResponseEntity.created(
            URI.create(
                    bookResource.getLink("self").getHref()))
            .build();
  }

  /**
   * Endpoint for getting book details.
   *
   * @param bookId Book Id
   * @return Book details
   */
  @GetMapping(value = "/{bookId}", produces = {MediaTypes.HAL_JSON_VALUE})
  public ResponseEntity<BookResource> getBook(@PathVariable final Long bookId) {
    Optional<Book> book = bookRepository.findById(bookId);

    if (book.isPresent()) {
      BookResource bookResource = bookToBookResourceAssembler.toResource(book.get());

      return ResponseEntity.ok(bookResource);
    } else {
      return ResponseEntity.notFound().build();
    }
  }

  /**
   * Endpoint for deleting book.
   *
   * @param bookId Book Id
   * @return Returns HTTP 204
   */
  @DeleteMapping(value = "/{bookId}", produces = {MediaTypes.HAL_JSON_VALUE})
  public ResponseEntity<?> deleteBook(@PathVariable final Long bookId) {
    Optional<Book> book = bookRepository.findById(bookId);

    if (book.isPresent()) {
      bookRepository.delete(book.get());

      return ResponseEntity.noContent().build();
    } else {
      return ResponseEntity.notFound().build();
    }
  }

  /**
   * Endpoint for updating book data.
   *
   * @param updates Map with update data
   * @param bookId  Book Id
   * @return Returns HTTP 204
   */
  @PatchMapping(value = "/{bookId}", consumes = MediaTypes.HAL_JSON_VALUE,
          produces = {MediaTypes.HAL_JSON_VALUE})
  public ResponseEntity<?> updateBook(
          @RequestBody final Map<String, Object> updates,
          @PathVariable("bookId") final Long bookId) {
    log.debug("Got map: {}", updates);

    Optional<Book> book = bookRepository.findById(bookId);

    if (book.isPresent()) {

      try {
        Book updatedBook = applyChanges(book.get(), updates);

        bookRepository.save(updatedBook);
      } catch (ConversionToJsonException e) {
        return ResponseEntity.badRequest().build();
      }

      return ResponseEntity.noContent().build();
    } else {
      return ResponseEntity.notFound().build();
    }
  }

  /**
   * Endpoint for replacing books.
   *
   * @param bookDto Book DTO
   * @param bookId  Book Id
   * @return Returns HTTP 204
   */
  @PutMapping(value = "/{bookId}", consumes = MediaTypes.HAL_JSON_VALUE,
          produces = {MediaTypes.HAL_JSON_VALUE})
  public ResponseEntity<?> replacingBook(
          @RequestBody BookDto bookDto,
          @PathVariable("bookId") final Long bookId) {
    log.debug("Got book: {}", bookDto);

    bookRepository.save(bookDto.getBook(bookId));

    return ResponseEntity.noContent().build();
  }

  private Book applyChanges(final Book book, final Map<String, Object> updates)
          throws ConversionToJsonException {

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
          throw new ConversionToJsonException();
      }
    }

    return builder.build();

  }

  private PagedResources<BookResource> getPagedBookResourcesWithLinks(
          final PagedResourcesAssembler<Book> assembler, final Page<Book> books) {
    Link self = ControllerLinkBuilder.linkTo(BooksController.class).withSelfRel();

    PagedResources<BookResource> booksPagedResources =
            assembler.toResource(books, bookToBookResourceAssembler, self);

    addLinksToPagedResources(booksPagedResources);

    return booksPagedResources;
  }

  /**
   * Adds link for search.
   *
   * @param booksPagedResources Paged Resources
   */
  private void addLinksToPagedResources(final PagedResources<BookResource> booksPagedResources) {
    booksPagedResources.add(
            ControllerLinkBuilder.linkTo(
                    ControllerLinkBuilder.methodOn(BooksController.class)
                            .search(null, null,
                                    new PagedResourcesAssembler<>(null, null)))
                    .withRel("search"));
  }


}
