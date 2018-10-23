package hr.mladen.cikara.spring.hal.browser.learning.test.book;

import hr.mladen.cikara.spring.hal.browser.learning.test.RestMediaTypes;
import java.net.URI;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PagedResourcesAssembler;
import org.springframework.hateoas.ExposesResourceFor;
import org.springframework.hateoas.Link;
import org.springframework.hateoas.PagedResources;
import org.springframework.hateoas.mvc.ControllerLinkBuilder;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
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

  @RequestMapping(method = RequestMethod.GET, produces = {RestMediaTypes.APPLICATION_HAL_JSON})
  ResponseEntity<PagedResources<BookResource>> findAll(
          final Pageable pageable,
          final PagedResourcesAssembler<Book> assembler) {

    Page<Book> books = bookRepository.findAll(pageable);

    PagedResources<BookResource> booksPagedResources =
            getPagedBookResourcesWithLinks(assembler, books);


    return ResponseEntity.ok(booksPagedResources);
  }

  @RequestMapping(
          value = "/search/title-contains", method = RequestMethod.GET,
          produces = {RestMediaTypes.APPLICATION_HAL_JSON})
  public ResponseEntity<PagedResources<BookResource>> search(
          @RequestParam(name = "query") final String query,
          final Pageable pageable, final PagedResourcesAssembler<Book> assembler) {

    Page<Book> books = bookRepository.findByTitleContaining(query, pageable);

    PagedResources<BookResource> booksPagedResources =
            getPagedBookResourcesWithLinks(assembler, books);

    return ResponseEntity.ok(booksPagedResources);
  }

  @RequestMapping(method = RequestMethod.POST, produces = {RestMediaTypes.APPLICATION_HAL_JSON})
  public ResponseEntity<?> createBook(@RequestBody BookDto bookDto) {
    log.debug("Got book: {}", bookDto);

    Book book = bookRepository.save(bookDto.getBook());


    BookResource bookResource = bookToBookResourceAssembler.toResource(book);

    return ResponseEntity.created(
            URI.create(
                    bookResource.getLink("self").getHref()))
            .build();
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
   * Adds link for search
   *
   * @param booksPagedResources Paged Resources
   */
  private void addLinksToPagedResources(final PagedResources<BookResource> booksPagedResources) {
    booksPagedResources.add(
            ControllerLinkBuilder.linkTo(
                    ControllerLinkBuilder.methodOn(BooksController.class)
                            .search(null, null, null)).withRel("search"));
  }


}
