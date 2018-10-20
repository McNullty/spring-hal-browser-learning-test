package hr.mladen.cikara.spring.hal.browser.learning.test.book;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PagedResourcesAssembler;
import org.springframework.hateoas.PagedResources;
import org.springframework.hateoas.Resource;
import org.springframework.hateoas.mvc.ControllerLinkBuilder;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("books")
public class BooksController {

  private final BookRepository bookRepository;

  public BooksController(final BookRepository bookRepository) {
    this.bookRepository = bookRepository;
  }

  @RequestMapping(method = RequestMethod.GET, produces = {"application/hal+json"})
  ResponseEntity<PagedResources<Resource<Book>>> findAll(
          Pageable pageable, PagedResourcesAssembler<Book> assembler) {

    Page<Book> books = bookRepository.findAll(pageable);
    PagedResources<Resource<Book>> booksPagedResources =
            assembler.toResource(books,
                    ControllerLinkBuilder.linkTo(BooksController.class).withSelfRel());

    return ResponseEntity.ok(booksPagedResources);
  }
}
