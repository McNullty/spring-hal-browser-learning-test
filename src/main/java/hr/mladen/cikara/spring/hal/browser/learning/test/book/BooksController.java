package hr.mladen.cikara.spring.hal.browser.learning.test.book;

import hr.mladen.cikara.spring.hal.browser.learning.test.profile.ProfileController;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PagedResourcesAssembler;
import org.springframework.hateoas.ExposesResourceFor;
import org.springframework.hateoas.Link;
import org.springframework.hateoas.PagedResources;
import org.springframework.hateoas.mvc.ControllerLinkBuilder;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("books")
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

  @RequestMapping(method = RequestMethod.GET, produces = {"application/hal+json"})
  ResponseEntity<PagedResources<BookResource>> findAll(
          Pageable pageable, PagedResourcesAssembler<Book> assembler) {

    Page<Book> books = bookRepository.findAll(pageable);

    Link booksLinkWithoutParameters =
            ControllerLinkBuilder.linkTo(BooksController.class).withSelfRel();
    Link self = new Link(booksLinkWithoutParameters.getHref() + "{?page,size,sort}")
            .withSelfRel();

    PagedResources<BookResource> booksPagedResources =
            assembler.toResource(books, bookToBookResourceAssembler, self);

    booksPagedResources.add(
            ControllerLinkBuilder.linkTo(
                    ControllerLinkBuilder.methodOn(BooksController.class)
                            .search(null, null, null)).withRel("search"));

    booksPagedResources.add(
            ControllerLinkBuilder.linkTo(
                    ControllerLinkBuilder.methodOn(ProfileController.class)
                            .booksProfile()).withRel("profile"));


    return ResponseEntity.ok(booksPagedResources);
  }

  @RequestMapping(
          value = "search", method = RequestMethod.GET, produces = {"application/hal+json"})
  ResponseEntity<PagedResources<BookResource>> search(
          String query, Pageable pageable, PagedResourcesAssembler<Book> assembler) {
    // TODO: Implement search functionality

    return ResponseEntity.ok(null);
  }
}
