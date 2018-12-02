package hr.mladen.cikara.spring.hal.browser.learning.test.book;

import java.net.URI;
import java.util.Map;
import javax.validation.Valid;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PagedResourcesAssembler;
import org.springframework.hateoas.ExposesResourceFor;
import org.springframework.hateoas.Link;
import org.springframework.hateoas.MediaTypes;
import org.springframework.hateoas.PagedResources;
import org.springframework.hateoas.mvc.ControllerLinkBuilder;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.util.Assert;
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

  private final BookService bookService;
  private final BookToBookResourceAssembler bookToBookResourceAssembler;

  /**
   * Constructor for BookController default implementation.
   *
   * @param bookService                 Book Service
   * @param bookToBookResourceAssembler Assembler that creates BookResources
   */
  public BooksController(
          final BookService bookService,
          final BookToBookResourceAssembler bookToBookResourceAssembler) {
    Assert.notNull(bookService, "Controller can't work without service");
    Assert.notNull(
            bookToBookResourceAssembler, "Controller can't work without resource assembler");

    this.bookService = bookService;
    this.bookToBookResourceAssembler = bookToBookResourceAssembler;
  }

  /**
   * Endpoint for listing all books.
   *
   * @param pageable  Pageable object, injected by Spring
   * @param assembler ResourcesAssembler object, injected by Spring
   * @return List of books
   */
  @GetMapping(produces = {MediaTypes.HAL_JSON_VALUE, MediaType.APPLICATION_JSON_VALUE})
  public ResponseEntity<PagedResources<BookResource>> findAll(
          final Pageable pageable,
          final PagedResourcesAssembler<Book> assembler) {

    Page<Book> books = bookService.findAll(pageable);

    PagedResources<BookResource> booksPagedResources =
            getPagedBookResourcesWithLinks(assembler, books);

    return ResponseEntity.ok(booksPagedResources);
  }

  /**
   * Searching for books that in titla have query string.
   *
   * @param query     Query that must match in book title
   * @param pageable  Pageable object, injected by Spring
   * @param assembler ResourcesAssembler object, injected by Spring
   * @return List of books that match query
   */
  @GetMapping(
          value = "/search/title-contains",
          produces = {MediaTypes.HAL_JSON_VALUE, MediaType.APPLICATION_JSON_VALUE})
  public ResponseEntity<PagedResources<BookResource>> search(
          @RequestParam(name = "query") final String query,
          final Pageable pageable, final PagedResourcesAssembler<Book> assembler) {

    Page<Book> books = bookService.findByTitleContaining(query, pageable);

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
  @PostMapping(consumes = {MediaTypes.HAL_JSON_VALUE, MediaType.APPLICATION_JSON_VALUE},
          produces = {MediaTypes.HAL_JSON_VALUE, MediaType.APPLICATION_JSON_VALUE})
  public ResponseEntity<?> createBook(@Valid @RequestBody BookDto bookDto) {
    log.debug("Got book: {}", bookDto);

    Book book = bookService.save(bookDto.getBook());

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
  @GetMapping(
          value = "/{bookId}",
          produces = {MediaTypes.HAL_JSON_VALUE, MediaType.APPLICATION_JSON_VALUE})
  public ResponseEntity<BookResource> getBook(@PathVariable final Long bookId)
          throws BookService.BookNotFoundException {
    Book book = bookService.getBook(bookId);

    BookResource bookResource = bookToBookResourceAssembler.toResource(book);

    return ResponseEntity.ok(bookResource);

  }

  /**
   * Endpoint for deleting book.
   *
   * @param bookId Book Id
   * @return Returns HTTP 204
   */
  @DeleteMapping(
          value = "/{bookId}",
          produces = {MediaTypes.HAL_JSON_VALUE, MediaType.APPLICATION_JSON_VALUE})
  public ResponseEntity<?> deleteBook(
          @PathVariable final Long bookId) throws BookService.BookNotFoundException {
    bookService.deleteBook(bookId);

    return ResponseEntity.noContent().build();
  }

  /**
   * Endpoint for updating book data.
   *
   * @param updates Map with update data
   * @param bookId  Book Id
   * @return Returns HTTP 204
   */
  @PatchMapping(value = "/{bookId}", consumes = MediaType.APPLICATION_JSON_VALUE,
          produces = {MediaTypes.HAL_JSON_VALUE, MediaType.APPLICATION_JSON_VALUE})
  public ResponseEntity<?> updateBook(
          @RequestBody final Map<String, Object> updates,
          @PathVariable("bookId") final Long bookId) throws BookService.BookNotFoundException {
    log.debug("Got map: {}", updates);

    Book updatedBook = bookService.updateBook(bookId, updates);

    BookResource bookResource = bookToBookResourceAssembler.toResource(updatedBook);

    return ResponseEntity.ok(bookResource);
  }

  /**
   * Endpoint for replacing books.
   *
   * @param bookDto Book DTO
   * @param bookId  Book Id
   * @return Returns HTTP 204
   */
  @PutMapping(value = "/{bookId}", consumes = MediaType.APPLICATION_JSON_VALUE,
          produces = {MediaTypes.HAL_JSON_VALUE, MediaType.APPLICATION_JSON_VALUE})
  public ResponseEntity<?> replaceBook(
          @Valid @RequestBody BookDto bookDto,
          @PathVariable("bookId") final Long bookId)
          throws WrongMethodUsedForCreatingBookException {
    log.debug("Got book: {}", bookDto);

    try {
      Book returnBook = bookService.replaceBook(bookId, bookDto);

      BookResource bookResource = bookToBookResourceAssembler.toResource(returnBook);

      return ResponseEntity.ok(bookResource);
    } catch (BookService.BookNotFoundException e) {
      throw new WrongMethodUsedForCreatingBookException();
    }
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

  public class WrongMethodUsedForCreatingBookException extends Exception {
  }
}
