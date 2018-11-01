package hr.mladen.cikara.spring.hal.browser.learning.test.book;

import java.net.URI;
import java.util.Map;
import java.util.Optional;
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
public class BooksControllerImpl implements BooksController {

  private final BookRepository bookRepository;
  private final BookToBookResourceAssembler bookToBookResourceAssembler;

  public BooksControllerImpl(
          final BookRepository bookRepository,
          final BookToBookResourceAssembler bookToBookResourceAssembler) {
    this.bookRepository = bookRepository;
    this.bookToBookResourceAssembler = bookToBookResourceAssembler;
  }

  @Override
  @GetMapping(produces = {MediaTypes.HAL_JSON_VALUE, MediaType.APPLICATION_JSON_VALUE})
  public ResponseEntity<PagedResources<BookResource>> findAll(
          final Pageable pageable,
          final PagedResourcesAssembler<Book> assembler) {

    Page<Book> books = bookRepository.findAll(pageable);

    PagedResources<BookResource> booksPagedResources =
            getPagedBookResourcesWithLinks(assembler, books);


    return ResponseEntity.ok(booksPagedResources);
  }


  @Override
  @GetMapping(
          value = "/search/title-contains",
          produces = {MediaTypes.HAL_JSON_VALUE, MediaType.APPLICATION_JSON_VALUE})
  public ResponseEntity<PagedResources<BookResource>> search(
          @RequestParam(name = "query") final String query,
          final Pageable pageable, final PagedResourcesAssembler<Book> assembler) {

    Page<Book> books = bookRepository.findByTitleContaining(query, pageable);

    PagedResources<BookResource> booksPagedResources =
            getPagedBookResourcesWithLinks(assembler, books);

    return ResponseEntity.ok(booksPagedResources);
  }

  @Override
  @PostMapping(consumes = {MediaTypes.HAL_JSON_VALUE, MediaType.APPLICATION_JSON_VALUE},
          produces = {MediaTypes.HAL_JSON_VALUE, MediaType.APPLICATION_JSON_VALUE})
  public ResponseEntity<?> createBook(@Valid @RequestBody BookDto bookDto) {
    log.debug("Got book: {}", bookDto);

    Book book = bookRepository.save(bookDto.getBook());


    BookResource bookResource = bookToBookResourceAssembler.toResource(book);

    return ResponseEntity.created(
            URI.create(
                    bookResource.getLink("self").getHref()))
            .build();
  }

  @Override
  @GetMapping(
          value = "/{bookId}",
          produces = {MediaTypes.HAL_JSON_VALUE, MediaType.APPLICATION_JSON_VALUE})
  public ResponseEntity<BookResource> getBook(@PathVariable final Long bookId) {
    Optional<Book> book = bookRepository.findById(bookId);

    if (book.isPresent()) {
      BookResource bookResource = bookToBookResourceAssembler.toResource(book.get());

      return ResponseEntity.ok(bookResource);
    } else {
      return ResponseEntity.notFound().build();
    }
  }

  @Override
  @DeleteMapping(
          value = "/{bookId}",
          produces = {MediaTypes.HAL_JSON_VALUE, MediaType.APPLICATION_JSON_VALUE})
  public ResponseEntity<?> deleteBook(@PathVariable final Long bookId) {
    Optional<Book> book = bookRepository.findById(bookId);

    if (book.isPresent()) {
      bookRepository.delete(book.get());

      return ResponseEntity.noContent().build();
    } else {
      return ResponseEntity.notFound().build();
    }
  }

  @Override
  @PatchMapping(value = "/{bookId}", consumes = MediaType.APPLICATION_JSON_VALUE,
          produces = {MediaTypes.HAL_JSON_VALUE, MediaType.APPLICATION_JSON_VALUE})
  public ResponseEntity<?> updateBook(
          @RequestBody final Map<String, Object> updates,
          @PathVariable("bookId") final Long bookId) {
    log.debug("Got map: {}", updates);

    Optional<Book> book = bookRepository.findById(bookId);

    if (book.isPresent()) {

      Book updatedBook = applyChanges(book.get(), updates);

      Book returnBook = bookRepository.save(updatedBook);

      BookResource bookResource = bookToBookResourceAssembler.toResource(returnBook);

      return ResponseEntity.ok(bookResource);

    } else {
      return ResponseEntity.notFound().build();
    }
  }

  @Override
  @PutMapping(value = "/{bookId}", consumes = MediaType.APPLICATION_JSON_VALUE,
          produces = {MediaTypes.HAL_JSON_VALUE, MediaType.APPLICATION_JSON_VALUE})
  public ResponseEntity<?> replaceBook(
          @Valid @RequestBody BookDto bookDto,
          @PathVariable("bookId") final Long bookId) {
    log.debug("Got book: {}", bookDto);

    Optional<Book> book = bookRepository.findById(bookId);

    if (book.isPresent()) {

      Book returnBook = bookRepository.save(bookDto.getBook(bookId));

      BookResource bookResource = bookToBookResourceAssembler.toResource(returnBook);

      return ResponseEntity.ok(bookResource);
    } else {
      // TODO: add instruction that you can't replace book and to use POST for creating new books
      return ResponseEntity.badRequest().build();
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

  private PagedResources<BookResource> getPagedBookResourcesWithLinks(
          final PagedResourcesAssembler<Book> assembler, final Page<Book> books) {
    Link self = ControllerLinkBuilder.linkTo(BooksControllerImpl.class).withSelfRel();

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
                    ControllerLinkBuilder.methodOn(BooksControllerImpl.class)
                            .search(null, null,
                                    new PagedResourcesAssembler<>(null, null)))
                    .withRel("search"));
  }


}
